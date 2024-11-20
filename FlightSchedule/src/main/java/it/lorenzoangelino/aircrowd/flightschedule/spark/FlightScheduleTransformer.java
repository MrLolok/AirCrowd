package it.lorenzoangelino.aircrowd.flightschedule.spark;

import it.lorenzoangelino.aircrowd.common.models.flights.enums.FlightType;
import it.lorenzoangelino.aircrowd.common.spark.SparkTransformer;
import it.lorenzoangelino.aircrowd.flightschedule.spark.udfs.FlightCodeGeneratorUDF;
import lombok.RequiredArgsConstructor;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.types.DataTypes;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;

import static org.apache.spark.sql.functions.*;

@RequiredArgsConstructor
public class FlightScheduleTransformer implements SparkTransformer {
    private final static DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private final boolean shiftDatesToday;
    private final boolean removeNameOfDay;

    @Override
    public Dataset<Row> transform(Dataset<Row> dataset) {
        registerUDFS(dataset);
        Dataset<Row> cleaned = removeUnusedColumns(dataset);
        Dataset<Row> renamed = renameRemainingOldColumns(cleaned);
        Dataset<Row> sorted = sortFlights(renamed);
        Dataset<Row> parsedFlightTypes = parseFlightType(sorted);
        Dataset<Row> updatedSeatsWithAverage = updateSeatsWithAverage(parsedFlightTypes);
        Dataset<Row> updatedFlightCodes = updateFlightCodes(updatedSeatsWithAverage);
        Dataset<Row> result = combineDateAndTime(updatedFlightCodes);
        if (shiftDatesToday)
            result = shiftDatesToToday(result);
        if (removeNameOfDay)
            result = removeNameOfDay(result);
        return result;
    }

    /**
     * Registra le UDFs necessarie alla trasformazione del dataset.
     */
    private void registerUDFS(Dataset<Row> dataset) {
        dataset
            .sparkSession()
            .udf()
            .register("generateFlightCode", new FlightCodeGeneratorUDF(), DataTypes.StringType);
    }

    /**
     * Rimuove le colonne inutilizzate dal dataset.
     */
    private Dataset<Row> removeUnusedColumns(Dataset<Row> dataset) {
        return dataset.drop("PARTENZA_ARRIVO");
    }

    /**
     * Rinomina le rimanenti vecchie colonne del dataset.
     */
    private Dataset<Row> renameRemainingOldColumns(Dataset<Row> dataset) {
        return dataset.withColumnRenamed("SEATS", "seats");
    }

    /**
     * Ordina i voli in base ai criteri specificati.
     */
    private Dataset<Row> sortFlights(Dataset<Row> dataset) {
        return dataset.orderBy(
            col("DATA"),
            col("FASCIA"),
            col("CODICE"),
            col("SEATS").desc());
    }

    /**
     * Calcola la media dei posti a sedere per giorno della settimana e fascia oraria
     * e aggiorna i voli con SEATS = 0 utilizzando questa media.
     */
    private Dataset<Row> updateSeatsWithAverage(Dataset<Row> dataset) {
        Dataset<Row> averageSeats = dataset
            .filter("SEATS > 0")
            .groupBy("GIORNO_NOME", "FASCIA_ORARIA")
            .agg(avg("SEATS").alias("AVG_SEATS"));

        Dataset<Row> flightsWithAvg = dataset.join(
            averageSeats,
            dataset.col("GIORNO_NOME").equalTo(averageSeats.col("GIORNO_NOME"))
                    .and(dataset.col("FASCIA_ORARIA").equalTo(averageSeats.col("FASCIA_ORARIA"))),
            "left");

        return flightsWithAvg
            .withColumn(
                    "SEATS",
                    when(col("SEATS").equalTo(0), round(col("AVG_SEATS"))).otherwise(col("SEATS")))
            .drop("AVG_SEATS");
    }

    /**
     * Genera e aggiorna il codice del volo nel dataset qualora non fosse presente.
     */
    private Dataset<Row> updateFlightCodes(Dataset<Row> dataset) {
        if (dataset.columns().length == 0 || !Arrays.asList(dataset.columns()).contains("code"))
            return dataset.withColumn("code", callUDF("generateFlightCode"));
        else
            return dataset.withColumn(
                "code",
                when(col("code").isNull().or(col("code")), callUDF("generateFlightCode")).otherwise(col("code")));
    }

    /**
     * Traduci il "codice" della tipologia nella reale tipologia del volo.
     */
    private Dataset<Row> parseFlightType(Dataset<Row> dataset) {
        return dataset
            .withColumn(
                "type",
                when(col("CODICE").equalTo("A"), FlightType.ARRIVAL.name())
                    .when(col("CODICE").equalTo("P"), FlightType.DEPARTURE.name())
                    .otherwise(FlightType.UNKNOWN.name()))
            .drop("CODICE");
    }

    /**
     * Combina la colonna "DATA" con la colonna "FASCIA_ORARIA" per creare un LocalDateTime.
     */
    private Dataset<Row> combineDateAndTime(Dataset<Row> dataset) {
        return dataset
            .withColumn(
                "datetime",
                concat(
                    date_format(to_date(col("DATA"), "dd/MM/yyyy"), "yyyy-MM-dd"),
                    lit("T"),
                    format_string("%02d", col("FASCIA_ORARIA")),
                    lit(":00:00")))
            .drop("DATE", "FASCIA_ORARIA");
    }

    /**
     * Sposta le date del dataset in modo che la prima corrisponda a oggi.
     */
    private Dataset<Row> shiftDatesToToday(Dataset<Row> dataset) {
        String firstDate = dataset.select("DATA").first().getString(0);
        LocalDate firstLocalDate = LocalDate.parse(firstDate, DATE_TIME_FORMATTER);
        LocalDate today = LocalDate.now();

        int daysDifference = Math.toIntExact(ChronoUnit.DAYS.between(firstLocalDate, today));

        String firstDayName = dataset.select("GIORNO_NOME").first().getString(0);
        int firstDayOfWeek = getDayOfWeek(firstDayName);
        int todayDayOfWeek = today.getDayOfWeek().getValue();
        int weekDaysDifference = todayDayOfWeek - firstDayOfWeek;
        if (weekDaysDifference < 0)
            weekDaysDifference += 7;
        daysDifference += weekDaysDifference;

        return dataset.withColumn(
                "DATA",
                date_format(date_add(to_date(col("DATA"), "dd/MM/yyyy"),daysDifference), "dd/MM/yyyy"));
    }

    /**
     * Rimuovi la colonna dei giorni della settimana, utilizzata precedentemente
     * per calcolare la media dei posti a sedere.
     */
    private Dataset<Row> removeNameOfDay(Dataset<Row> dataset) {
        return dataset.drop("GIORNO_NOME");
    }

    /**
     * Restituisce il valore numerico del giorno della settimana (1 = Lunedì, 7 = Domenica).
     */
    private int getDayOfWeek(String dayName) {
        return switch (dayName.toUpperCase()) {
            case "LUNEDÌ" -> 1;
            case "MARTEDÌ" -> 2;
            case "MERCOLEDÌ" -> 3;
            case "GIOVEDÌ" -> 4;
            case "VENERDÌ" -> 5;
            case "SABATO" -> 6;
            case "DOMENICA" -> 7;
            default -> throw new IllegalArgumentException("Invalid day name: " + dayName);
        };
    }
}
