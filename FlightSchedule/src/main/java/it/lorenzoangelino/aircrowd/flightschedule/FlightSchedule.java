package it.lorenzoangelino.aircrowd.flightschedule;

import it.lorenzoangelino.aircrowd.common.configs.ConfigProvider;
import it.lorenzoangelino.aircrowd.flightschedule.configs.ConfigFlightScheduleSettings;
import it.lorenzoangelino.aircrowd.flightschedule.spark.etl.FlightScheduleETL;
import it.lorenzoangelino.aircrowd.flightschedule.spark.etl.SparkETL;
import it.lorenzoangelino.aircrowd.flightschedule.spark.SparkConfig;
import org.apache.spark.sql.SparkSession;

public final class FlightSchedule {
    public static void main(String[] args) {
        ConfigFlightScheduleSettings settings = ConfigProvider.getInstance().loadConfig("flight-schedule", ConfigFlightScheduleSettings.class);
        SparkSession session = SparkConfig.getSparkSession();
        SparkETL etl = new FlightScheduleETL(session);
        etl.process(settings.source(), settings.destination());
    }
}
