package it.lorenzoangelino.aircrowd.flightschedule.spark;

import it.lorenzoangelino.aircrowd.flightschedule.exceptions.FlightScheduleException;
import lombok.extern.slf4j.Slf4j;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SaveMode;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class FlightScheduleLoader implements SparkDataLoader {

    @Override
    @CacheEvict(
            cacheNames = {"flight-etl-cache", "flight-extract-cache"},
            allEntries = true)
    public void load(Dataset<Row> dataset, String destination) {
        log.info("Loading data to destination: {}", destination);

        try {
            dataset.write().mode(SaveMode.Overwrite).option("header", "true").csv(destination);

            log.info("Successfully loaded {} records to {}", dataset.count(), destination);

        } catch (Exception e) {
            log.error("Failed to load data to destination: {}", destination, e);
            throw new FlightScheduleException("Data loading failed", e);
        }
    }
}
