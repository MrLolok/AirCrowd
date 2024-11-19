package it.lorenzoangelino.aircrowd.flightschedule.spark.etl;

import it.lorenzoangelino.aircrowd.flightschedule.spark.extractors.SparkExtractor;
import it.lorenzoangelino.aircrowd.flightschedule.spark.loaders.SparkLoader;
import it.lorenzoangelino.aircrowd.flightschedule.spark.transformers.SparkTransformer;

public interface SparkETL {
    void process(String source, String destination);

    SparkExtractor getExtractor();

    SparkTransformer getTransformer();

    SparkLoader getLoader();

    void setExtractor(SparkExtractor extractor);

    void setTransformer(SparkTransformer transformer);

    void setLoader(SparkLoader loader);
}
