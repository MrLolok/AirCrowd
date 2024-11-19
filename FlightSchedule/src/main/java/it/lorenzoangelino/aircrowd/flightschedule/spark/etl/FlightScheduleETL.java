package it.lorenzoangelino.aircrowd.flightschedule.spark.etl;

import it.lorenzoangelino.aircrowd.flightschedule.spark.extractors.FlightScheduleTableExtractor;
import it.lorenzoangelino.aircrowd.flightschedule.spark.extractors.SparkExtractor;
import it.lorenzoangelino.aircrowd.flightschedule.spark.loaders.FlightScheduleLoader;
import it.lorenzoangelino.aircrowd.flightschedule.spark.loaders.SparkLoader;
import it.lorenzoangelino.aircrowd.flightschedule.spark.transformers.FlightScheduleTransformer;
import it.lorenzoangelino.aircrowd.flightschedule.spark.transformers.SparkTransformer;
import lombok.Getter;
import lombok.Setter;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SparkSession;

@Getter @Setter
public class FlightScheduleETL implements SparkETL {
    private SparkExtractor extractor;
    private SparkTransformer transformer;
    private SparkLoader loader;

    public FlightScheduleETL(SparkSession spark) {
        this.extractor = new FlightScheduleTableExtractor(spark);
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
