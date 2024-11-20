package it.lorenzoangelino.aircrowd.flightschedule.spark;

import it.lorenzoangelino.aircrowd.common.configs.ConfigProvider;
import it.lorenzoangelino.aircrowd.flightschedule.configs.ConfigSparkSettings;
import it.lorenzoangelino.aircrowd.flightschedule.spark.udfs.FlightCodeGeneratorUDF;
import org.apache.spark.sql.SparkSession;
import org.apache.spark.sql.UDFRegistration;
import org.apache.spark.sql.types.DataTypes;

import java.util.Random;

public final class SparkConfig {
    private final static ConfigSparkSettings SPARK_SETTINGS = ConfigProvider.getInstance().loadConfig("spark", ConfigSparkSettings.class);

    public static SparkSession getSparkSession() {
        SparkSession.Builder builder = SparkSession.builder()
            .appName(SPARK_SETTINGS.appName())
            .master(SPARK_SETTINGS.master())
            .enableHiveSupport();
        SPARK_SETTINGS.config().forEach(builder::config);

        SparkSession session = builder.getOrCreate();
        registerUDFS(session);
        return session;
    }

    private static void registerUDFS(SparkSession session) {
        UDFRegistration registration = session.udf();
        registration.register("generateFlightCode", new FlightCodeGeneratorUDF(), DataTypes.StringType);
    }
}
