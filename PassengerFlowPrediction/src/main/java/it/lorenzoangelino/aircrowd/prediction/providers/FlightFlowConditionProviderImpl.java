package it.lorenzoangelino.aircrowd.prediction.providers;

import it.lorenzoangelino.aircrowd.common.models.flights.FlightFlowData;
import it.lorenzoangelino.aircrowd.prediction.models.conditions.Condition;
import it.lorenzoangelino.aircrowd.prediction.models.conditions.individual.impl.FlightFlowCondition;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SparkSession;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

public class FlightFlowConditionProviderImpl implements FlightFlowConditionProvider {
    private final static DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private final static String FLIGHT_DATA_TABLE_NAME = "flight_data";

    private final SparkSession sparkSession;

    public FlightFlowConditionProviderImpl(SparkSession sparkSession) {
        this.sparkSession = sparkSession;
    }

    @Override
    public Optional<Condition> getCondition(LocalDateTime start, LocalDateTime end) {
        List<Row> rows = getFlightDataAsList(start, end);
        FlightFlowData flightFlowData = FlightsDataExtractor.extract(rows);
        if (flightFlowData.flights().isEmpty())
            return Optional.empty();
        Condition condition = new FlightFlowCondition(flightFlowData);
        return Optional.of(condition);
    }

    private List<Row> getFlightDataAsList(LocalDateTime start, LocalDateTime end) {
        Dataset<Row> flightData = getFlightDataInRange(start, end);
        return flightData.collectAsList();
    }

    private Dataset<Row> getFlightDataInRange(LocalDateTime start, LocalDateTime end) {
        if (start.isAfter(end))
            throw new IllegalArgumentException("Start date cannot be after end date.");

        String startDateFormatted = start.format(DATE_TIME_FORMATTER), endDateFormatted = end.format(DATE_TIME_FORMATTER);
        String query = String.format(
                "SELECT * FROM %s WHERE flight_time >= '%s' AND flight_time <= '%s'",
                FLIGHT_DATA_TABLE_NAME, startDateFormatted, endDateFormatted);
        return sparkSession.sql(query);
    }

}
