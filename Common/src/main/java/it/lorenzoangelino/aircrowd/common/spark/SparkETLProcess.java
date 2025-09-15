package it.lorenzoangelino.aircrowd.common.spark;

public interface SparkETLProcess {
    void process(String source, String destination);

    SparkDataExtractor getExtractor();

    SparkDataTransformer getTransformer();

    SparkDataLoader getLoader();

    void setExtractor(SparkDataExtractor extractor);

    void setTransformer(SparkDataTransformer transformer);

    void setLoader(SparkDataLoader loader);

    void cleanup();
}
