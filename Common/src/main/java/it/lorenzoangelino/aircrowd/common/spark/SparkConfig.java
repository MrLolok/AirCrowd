package it.lorenzoangelino.aircrowd.common.spark;

import it.lorenzoangelino.aircrowd.common.configs.ConfigProvider;
import it.lorenzoangelino.aircrowd.common.configs.defaults.ConfigSparkSettings;
import org.apache.spark.sql.SparkSession;

public final class SparkConfig {
    private final static ConfigSparkSettings SPARK_SETTINGS = ConfigProvider.getInstance().loadConfig("spark", ConfigSparkSettings.class);

    public static SparkSession getSparkSession() {
        SparkSession.Builder builder = SparkSession.builder()
            .appName(SPARK_SETTINGS.appName())
            .master(SPARK_SETTINGS.master())
            .enableHiveSupport();
        SPARK_SETTINGS.config().forEach(builder::config);
        return builder.getOrCreate();
    }
}
