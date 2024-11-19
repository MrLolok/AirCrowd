package it.lorenzoangelino.aircrowd.flightschedule.spark.transformers;

import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;

@FunctionalInterface
public interface SparkTransformer {
    Dataset<Row> transform(Dataset<Row> origin);
}
