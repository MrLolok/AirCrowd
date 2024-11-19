package it.lorenzoangelino.aircrowd.flightschedule.spark.extractors;

import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;

@FunctionalInterface
public interface SparkExtractor {
    Dataset<Row> read(String path);
}
