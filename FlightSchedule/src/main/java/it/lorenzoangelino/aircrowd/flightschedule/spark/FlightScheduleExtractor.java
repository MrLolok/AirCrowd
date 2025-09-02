package it.lorenzoangelino.aircrowd.flightschedule.spark;

import it.lorenzoangelino.aircrowd.flightschedule.converters.CSVConverter;
import it.lorenzoangelino.aircrowd.flightschedule.exceptions.FlightScheduleException;
import java.io.File;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SparkSession;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class FlightScheduleExtractor implements SparkDataExtractor {
    private final SparkSession sparkSession;
    private final CSVConverter csvConverter;

    public FlightScheduleExtractor(SparkSession sparkSession, CSVConverter csvConverter) {
        this.sparkSession = sparkSession;
        this.csvConverter = csvConverter;
    }

    @Override
    @Cacheable(cacheNames = "flight-extract-cache", key = "#source")
    public Dataset<Row> extract(String source) {
        log.info("Extracting data from source: {}", source);

        try {
            if (source.endsWith(".csv")) {
                return sparkSession
                        .read()
                        .option("header", "true")
                        .option("inferSchema", "true")
                        .csv(source);
            } else if (source.endsWith(".xlsx")) {
                Optional<File> csvFile = csvConverter.convertToCsv(source);
                if (csvFile.isEmpty()) {
                    throw new FlightScheduleException("Failed to convert XLSX to CSV: " + source);
                }
                return sparkSession
                        .read()
                        .option("header", "true")
                        .option("inferSchema", "true")
                        .csv(csvFile.get().getPath());
            } else {
                throw new FlightScheduleException("Unsupported file format: " + source);
            }
        } catch (Exception e) {
            log.error("Failed to extract data from source: {}", source, e);
            throw new FlightScheduleException("Data extraction failed", e);
        }
    }
}
