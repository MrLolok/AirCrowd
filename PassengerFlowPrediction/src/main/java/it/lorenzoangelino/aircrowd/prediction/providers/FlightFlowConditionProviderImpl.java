package it.lorenzoangelino.aircrowd.prediction.providers;

import it.lorenzoangelino.aircrowd.common.models.flights.FlightFlowData;
import it.lorenzoangelino.aircrowd.prediction.factories.ConditionFactory;
import it.lorenzoangelino.aircrowd.prediction.models.conditions.Condition;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SparkSession;

@Service
@RequiredArgsConstructor
public class FlightFlowConditionProviderImpl implements FlightFlowConditionProvider {
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private final SparkSession sparkSession;
    private final ConditionFactory conditionFactory;
    
    @Value("${condition-providers.flight-flow.table-name}")
    private String flightDataTableName;
    
    @Value("${condition-providers.flight-flow.spark-query.datetime-column}")
    private String datetimeColumn;

    @Override
    public Optional<Condition> getCondition(LocalDateTime start, LocalDateTime end) {
        List<Row> rows = getFlightDataAsList(start, end);
        FlightFlowData flightFlowData = FlightsDataExtractor.extract(rows);
        if (flightFlowData.flights().isEmpty()) return Optional.empty();
        Condition condition = conditionFactory.createFlightFlowCondition(flightFlowData);
        return Optional.of(condition);
    }

    private List<Row> getFlightDataAsList(LocalDateTime start, LocalDateTime end) {
        Dataset<Row> flightData = getFlightDataInRange(start, end);
        return flightData.collectAsList();
    }

    private Dataset<Row> getFlightDataInRange(LocalDateTime start, LocalDateTime end) {
        if (start.isAfter(end)) throw new IllegalArgumentException("Start date cannot be after end date.");

        String startDateFormatted = start.format(DATE_TIME_FORMATTER),
                endDateFormatted = end.format(DATE_TIME_FORMATTER);
        String query = String.format(
                "SELECT * FROM %s WHERE %s >= '%s' AND %s <= '%s'",
                flightDataTableName, datetimeColumn, startDateFormatted, datetimeColumn, endDateFormatted);
        return sparkSession.sql(query);
    }
}
