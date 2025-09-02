package it.lorenzoangelino.aircrowd.flightschedule.config;

import java.util.concurrent.TimeUnit;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "flight-schedule")
public record FlightScheduleConfigurationProperties(
        Etl etl,
        Processing processing,
        Schedule schedule) {

    public FlightScheduleConfigurationProperties {
        if (etl == null) {
            etl = new Etl("data/flights.xlsx", "hive_catalog.db_name.flight_schedule");
        }
        if (processing == null) {
            processing = new Processing(1000, true);
        }
        if (schedule == null) {
            schedule = new Schedule(0, 30, TimeUnit.MINUTES);
        }
    }

    public record Etl(String source, String destination) {
        public Etl {
            if (source == null || source.isBlank()) {
                source = "data/flights.xlsx";
            }
            if (destination == null || destination.isBlank()) {
                destination = "hive_catalog.db_name.flight_schedule";
            }
        }
    }

    public record Processing(int batchSize, boolean validateData) {
        public Processing {
            if (batchSize <= 0) {
                batchSize = 1000;
            }
        }
    }

    public record Schedule(long delay, long period, TimeUnit unit) {
        public Schedule {
            if (delay < 0) delay = 0;
            if (period <= 0) period = 30;
            if (unit == null) unit = TimeUnit.MINUTES;
        }
    }
}