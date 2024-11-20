package it.lorenzoangelino.aircrowd.common.spark;

import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;

@FunctionalInterface
public interface SparkExtractor {
    Dataset<Row> read(String path);
}
