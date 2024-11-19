package it.lorenzoangelino.aircrowd.flightschedule.spark.loaders;

import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;

public interface SparkLoader {
    void load(Dataset<Row> dataset, String destination);
}
