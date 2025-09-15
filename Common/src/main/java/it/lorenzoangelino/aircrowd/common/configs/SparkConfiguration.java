package it.lorenzoangelino.aircrowd.common.configs;

import lombok.extern.slf4j.Slf4j;
import org.apache.spark.sql.SparkSession;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import java.util.concurrent.atomic.AtomicReference;

@Configuration
@Slf4j
public class SparkConfiguration {

    @Value("${spark.master:local[*]}")
    private String sparkMaster;
    
    @Value("${spark.app-name:AirCrowd-Default}")
    private String sparkAppName;

    @Value("${spark.warehouse.path:file:///tmp/spark-warehouse}")
    private String sparkWarehousePath;
    
    @Value("${spark.executor.memory:2g}")
    private String executorMemory;
    
    @Value("${spark.driver.memory:1g}")
    private String driverMemory;
    
    @Value("${spark.sql.shuffle.partitions:8}")
    private String shufflePartitions;

    private final AtomicReference<SparkSession> sparkSessionRef = new AtomicReference<>();

    @Bean
    @Primary
    public SparkSession sparkSession() {
        return sparkSessionRef.updateAndGet(current -> {
            if (current == null || current.sparkContext().isStopped()) {
                log.info("Creating new optimized Spark session");
                return createOptimizedSession();
            }
            return current;
        });
    }

    /**
     * Creates an optimized Spark session with performance tuning
     */
    private SparkSession createOptimizedSession() {
        try {
            SparkSession.Builder builder = SparkSession.builder()
                    .appName(sparkAppName)
                    .master(sparkMaster)

                    // Memory Management
                    .config("spark.executor.memory", executorMemory)
                    .config("spark.driver.memory", driverMemory)

                    // Performance Optimization
                    .config("spark.sql.adaptive.advisoryPartitionSizeInBytes", "128MB")
                    .config("spark.sql.adaptive.skewJoin.enabled", "true")
                    .config("spark.sql.cbo.enabled", "true")
                    .config("spark.sql.cbo.joinReorder.enabled", "true")

                    // Serialization
                    .config("spark.serializer", "org.apache.spark.serializer.KryoSerializer")

                    // Network and Shuffle
                    .config("spark.network.timeout", "300s")
                    .config("spark.sql.shuffle.partitions", shufflePartitions)
                    .config("spark.sql.adaptive.shuffle.targetPostShuffleInputSize", "64MB")

                    // Iceberg Optimization
                    .config("spark.sql.extensions", "org.apache.iceberg.spark.extensions.IcebergSparkSessionExtensions")
                    .config("spark.sql.catalog.iceberg", "org.apache.iceberg.spark.SparkCatalog")
                    .config("spark.sql.catalog.iceberg.type", "hadoop")
                    .config("spark.sql.catalog.iceberg.warehouse", sparkWarehousePath)
                    .config("spark.sql.defaultCatalog", "iceberg")

                    // Custom configurations
                    .config("spark.app.name", sparkAppName);

            SparkSession session = builder.getOrCreate();
            log.info("Successfully created optimized Spark session: {}", sparkAppName);
            return session;
        } catch (Exception e) {
            log.error("Failed to create optimized Spark session: {}", sparkAppName, e);
            throw new RuntimeException("Failed to create Spark session", e);
        }
    }
}