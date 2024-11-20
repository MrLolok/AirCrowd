package it.lorenzoangelino.aircrowd.common.spark;

public interface SparkETLProcess {
    void process(String source, String destination);

    SparkExtractor getExtractor();

    SparkTransformer getTransformer();

    SparkLoader getLoader();

    void setExtractor(SparkExtractor extractor);

    void setTransformer(SparkTransformer transformer);

    void setLoader(SparkLoader loader);
}
