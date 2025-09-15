package it.lorenzoangelino.aircrowd.flightschedule.spark;

import it.lorenzoangelino.aircrowd.common.spark.SparkDataExtractor;
import it.lorenzoangelino.aircrowd.common.spark.SparkDataLoader;
import it.lorenzoangelino.aircrowd.common.spark.SparkDataTransformer;
import it.lorenzoangelino.aircrowd.common.spark.SparkETLProcess;
import it.lorenzoangelino.aircrowd.flightschedule.exceptions.FlightScheduleException;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SparkSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@Getter @Setter
@Cacheable(cacheNames = "flight-etl-cache")
public class FlightScheduleETLProcess implements SparkETLProcess {
    private final SparkSession sparkSession;
    private SparkDataExtractor extractor;
    private SparkDataTransformer transformer;
    private SparkDataLoader loader;

    @Autowired
    public FlightScheduleETLProcess(
            SparkSession sparkSession,
            @Qualifier("flightScheduleExtractor")
            SparkDataExtractor extractor,
            @Qualifier("flightScheduleTransformer")
            SparkDataTransformer transformer,
            @Qualifier("flightScheduleLoader")
            SparkDataLoader loader) {
        this.sparkSession = sparkSession;
        this.extractor = extractor;
        this.transformer = transformer;
        this.loader = loader;
    }

    @Override
    public void process(String source, String destination) {
        try {
            log.info("Starting Flight Schedule ETL process: {} -> {}", source, destination);

            Dataset<Row> extracted = extractor.extract(source);
            log.info("Extracted {} records from source", extracted.count());

            Dataset<Row> transformed = transformer.transform(extracted);
            log.info("Transformed {} records", transformed.count());

            loader.load(transformed, destination);
            log.info("Successfully loaded data to target: {}", destination);

        } catch (Exception e) {
            log.error("ETL process failed for source: {} target: {}", source, destination, e);
            throw new FlightScheduleException("ETL process failed", e);
        }
    }

    @CacheEvict(cacheNames = "flight-etl-cache", allEntries = true)
    @Override
    public void cleanup() {
        try {
            log.info("Cleaning up ETL process resources");
        } catch (Exception e) {
            log.error("Error during ETL cleanup", e);
        }
    }
}
