package it.lorenzoangelino.aircrowd.flightschedule.spark.loaders;

import lombok.RequiredArgsConstructor;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SaveMode;

@RequiredArgsConstructor
public class FlightScheduleLoader implements SparkLoader {
    @Override
    public void load(Dataset<Row> dataset, String destination) {
        dataset.write()
            .format("iceberg")
            .mode(SaveMode.Append)
            .partitionBy("DATA")
            .save(destination);
    }
}
