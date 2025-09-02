package it.lorenzoangelino.aircrowd.flightschedule.spark;

import it.lorenzoangelino.aircrowd.common.spark.SparkETLProcess;
import it.lorenzoangelino.aircrowd.flightschedule.exceptions.FlightScheduleException;
import lombok.extern.slf4j.Slf4j;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SparkSession;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@Cacheable(cacheNames = "flight-etl-cache")
public class FlightScheduleETLProcess implements SparkETLProcess {
    private final SparkSession sparkSession;
    private final FlightScheduleExtractor extractor;
    private final FlightScheduleTransformer transformer;
    private final FlightScheduleLoader loader;

    public FlightScheduleETLProcess(
            SparkSession sparkSession,
            FlightScheduleExtractor extractor,
            FlightScheduleTransformer transformer,
            FlightScheduleLoader loader) {
        this.sparkSession = sparkSession;
        this.extractor = extractor;
        this.transformer = transformer;
        this.loader = loader;
    }

    @Override
    public void run(String source, String target) {
        try {
            log.info("Starting Flight Schedule ETL process: {} -> {}", source, target);

            Dataset<Row> extracted = extractor.extract(source);
            log.info("Extracted {} records from source", extracted.count());

            Dataset<Row> transformed = transformer.transform(extracted);
            log.info("Transformed {} records", transformed.count());

            loader.load(transformed, target);
            log.info("Successfully loaded data to target: {}", target);

        } catch (Exception e) {
            log.error("ETL process failed for source: {} target: {}", source, target, e);
            throw new FlightScheduleException("ETL process failed", e);
        }
    }

    @Override
    @CacheEvict(cacheNames = "flight-etl-cache", allEntries = true)
    public void cleanup() {
        try {
            log.info("Cleaning up ETL process resources");
            // Additional cleanup if needed
        } catch (Exception e) {
            log.error("Error during ETL cleanup", e);
        }
    }
}
