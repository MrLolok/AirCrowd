package it.lorenzoangelino.aircrowd.flightschedule.spark;

import it.lorenzoangelino.aircrowd.common.spark.SparkETLProcess;
import it.lorenzoangelino.aircrowd.common.spark.SparkExtractor;
import it.lorenzoangelino.aircrowd.common.spark.SparkLoader;
import it.lorenzoangelino.aircrowd.common.spark.SparkTransformer;
import lombok.Getter;
import lombok.Setter;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SparkSession;

@Getter
@Setter
public class FlightScheduleETLProcess implements SparkETLProcess {
    private SparkExtractor extractor;
    private SparkTransformer transformer;
    private SparkLoader loader;

    public FlightScheduleETLProcess(SparkSession spark) {
        this.extractor = new FlightScheduleExtractor(spark);
        this.transformer = new FlightScheduleTransformer(true, true);
        this.loader = new FlightScheduleLoader();
    }

    @Override
    public void process(String source, String destination) {
        Dataset<Row> dataset = extractor.read(source);
        dataset = transformer.transform(dataset);
        loader.load(dataset, destination);
    }
}
