package it.lorenzoangelino.aircrowd.flightschedule.spark.transformers;

import lombok.RequiredArgsConstructor;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

import static org.apache.spark.sql.functions.*;

@RequiredArgsConstructor
public class FlightScheduleTransformer implements SparkTransformer {
    private final static DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private final boolean shiftDatesToday;
    private final boolean removeNameOfDay;

    @Override
    public Dataset<Row> transform(Dataset<Row> dataset) {
        Dataset<Row> cleaned = removeUnusedColumns(dataset);
        Dataset<Row> sorted = sortFlights(cleaned);
        Dataset<Row> result = updateSeatsWithAverage(sorted);
        if (shiftDatesToday)
            result = shiftDatesToToday(result);
        if (removeNameOfDay)
            result = removeNameOfDay(result);
        return result;
    }

    /**
     * Rimuove le colonne inutilizzate dal dataset.
     */
    private Dataset<Row> removeUnusedColumns(Dataset<Row> dataset) {
        return dataset.drop("PARTENZA_ARRIVO");
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
     * Sposta le date del dataset in modo che la prima corrisponda a oggi.
     */
    private Dataset<Row> shiftDatesToToday(Dataset<Row> dataset) {
        String firstDate = dataset.select("DATA").first().getString(0);
        LocalDate firstLocalDate = LocalDate.parse(firstDate, DATE_TIME_FORMATTER);
        LocalDate today = LocalDate.now();

        int daysDifference = Math.toIntExact(ChronoUnit.DAYS.between(firstLocalDate, today));
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
}
