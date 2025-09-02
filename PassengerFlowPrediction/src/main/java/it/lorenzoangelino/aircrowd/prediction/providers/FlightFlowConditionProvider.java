package it.lorenzoangelino.aircrowd.prediction.providers;

import it.lorenzoangelino.aircrowd.common.models.flights.FlightData;
import it.lorenzoangelino.aircrowd.common.models.flights.FlightFlowData;
import it.lorenzoangelino.aircrowd.common.models.flights.enums.FlightType;
import java.time.LocalDateTime;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.apache.spark.sql.Row;

public interface FlightFlowConditionProvider extends ConditionProvider {
    @Slf4j
    class FlightsDataExtractor {

        public static FlightFlowData extract(List<Row> rows) {
            List<FlightData> flightDataList = rows.stream()
                    .map(FlightFlowConditionProvider.FlightsDataExtractor::extract)
                    .toList();
            return new FlightFlowData(flightDataList);
        }

        public static FlightData extract(Row row) {
            String code = row.getString(row.fieldIndex("code"));
            LocalDateTime datetime =
                    row.getTimestamp(row.fieldIndex("datetime")).toLocalDateTime();
            short seats = row.getShort(row.fieldIndex("seats"));
            String typeRaw = row.getString(row.fieldIndex("type"));
            FlightType type = FlightType.UNKNOWN;
            try {
                type = FlightType.valueOf(typeRaw.toUpperCase());
            } catch (IllegalArgumentException e) {
                log.warn("Unknown flight type {}", typeRaw);
            }
            return new FlightData(code, type, datetime, seats);
        }
    }
}
