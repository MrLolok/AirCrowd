package it.lorenzoangelino.aircrowd.common.spark;

import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;

public interface SparkDataExtractor {
    Dataset<Row> extract(String source);
}