package it.lorenzoangelino.aircrowd.common.spark;

import it.lorenzoangelino.aircrowd.common.configs.ConfigProvider;
import it.lorenzoangelino.aircrowd.common.configs.defaults.ConfigSparkSettings;
import java.util.concurrent.atomic.AtomicReference;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.spark.sql.SparkSession;

public final class SparkConfig {
    private static final Logger LOGGER = LogManager.getLogger(SparkConfig.class);
    private static final AtomicReference<SparkSession> INSTANCE = new AtomicReference<>();

    private SparkConfig() {
        throw new UnsupportedOperationException("SparkConfig is a utility class and cannot be instantiated");
    }

    /**
     * Gets the default optimized Spark session with connection pooling
     */
    public static SparkSession getSparkSession() {
        return INSTANCE.updateAndGet(current -> {
            if (current == null || current.sparkContext().isStopped()) {
                LOGGER.info("Creating new optimized Spark session");
                return createOptimizedSession();
            }
            return current;
        });
    }

    /**
     * Creates an optimized Spark session with performance tuning
     */
    private static SparkSession createOptimizedSession() {
        try {
            ConfigSparkSettings config = ConfigProvider.getInstance().loadConfig("spark", ConfigSparkSettings.class);

            SparkSession.Builder builder = SparkSession.builder()
                    .appName("AirCrowd-Default")
                    .master(config.master())

                    // Memory Management
                    .config("spark.executor.memory", "2g")
                    .config("spark.driver.memory", "1g")
                    .config("spark.executor.memoryFraction", "0.8")
                    .config("spark.sql.adaptive.enabled", "true")
                    .config("spark.sql.adaptive.coalescePartitions.enabled", "true")

                    // Performance Optimization
                    .config("spark.sql.adaptive.advisoryPartitionSizeInBytes", "128MB")
                    .config("spark.sql.adaptive.skewJoin.enabled", "true")
                    .config("spark.sql.cbo.enabled", "true")
                    .config("spark.sql.cbo.joinReorder.enabled", "true")

                    // Serialization
                    .config("spark.serializer", "org.apache.spark.serializer.KryoSerializer")
                    .config("spark.sql.execution.arrow.pyspark.enabled", "true")

                    // Network and Shuffle
                    .config("spark.network.timeout", "300s")
                    .config("spark.sql.shuffle.partitions", "200")
                    .config("spark.sql.adaptive.shuffle.targetPostShuffleInputSize", "64MB")

                    // Checkpointing and Recovery
                    .config("spark.sql.recovery.checkpointInterval", "10")
                    .config("spark.sql.streaming.checkpointLocation", "/tmp/spark-checkpoints/" + "AirCrowd-Default")

                    // Dynamic Allocation
                    .config("spark.dynamicAllocation.enabled", "true")
                    .config("spark.dynamicAllocation.minExecutors", "1")
                    .config("spark.dynamicAllocation.maxExecutors", "10")
                    .config("spark.dynamicAllocation.initialExecutors", "2")

                    // Logging and Monitoring
                    .config("spark.eventLog.enabled", "true")
                    .config("spark.eventLog.dir", "/tmp/spark-events")
                    .config("spark.sql.streaming.metricsEnabled", "true")

                    // Iceberg Optimization
                    .config("spark.sql.catalog.spark_catalog", "org.apache.iceberg.spark.SparkSessionCatalog")
                    .config("spark.sql.catalog.spark_catalog.type", "hive")
                    .config("spark.sql.catalog.iceberg", "org.apache.iceberg.spark.SparkCatalog")
                    .config("spark.sql.catalog.iceberg.type", "rest")
                    .config("spark.sql.catalog.iceberg.uri", "http://localhost:8181")
                    .config("spark.sql.catalog.iceberg.io-impl", "org.apache.iceberg.aws.s3.S3FileIO")
                    .config("spark.sql.catalog.iceberg.warehouse", "s3://warehouse/")
                    .config("spark.sql.catalog.iceberg.s3.endpoint", "http://localhost:9000")

                    // Custom configurations from config file
                    .config("spark.app.name", "AirCrowd-Default")
                    .enableHiveSupport();

            // Apply additional configurations from config file
            if (config.config() != null) {
                config.config().forEach(builder::config);
            }

            SparkSession session = builder.getOrCreate();

            // Set adaptive query execution
            session.conf().set("spark.sql.adaptive.enabled", "true");
            session.conf().set("spark.sql.adaptive.coalescePartitions.enabled", "true");

            LOGGER.info("Successfully created optimized Spark session: {}", "AirCrowd-Default");

            return session;

        } catch (Exception e) {
            LOGGER.error("Failed to create optimized Spark session: {}", "AirCrowd-Default", e);
            throw new RuntimeException("Failed to create Spark session", e);
        }
    }
}
