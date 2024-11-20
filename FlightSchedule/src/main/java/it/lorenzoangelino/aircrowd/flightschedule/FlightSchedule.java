package it.lorenzoangelino.aircrowd.flightschedule;

import it.lorenzoangelino.aircrowd.common.configs.ConfigProvider;
import it.lorenzoangelino.aircrowd.common.spark.SparkConfig;
import it.lorenzoangelino.aircrowd.common.spark.SparkETLProcess;
import it.lorenzoangelino.aircrowd.flightschedule.configs.ConfigFlightScheduleSettings;
import it.lorenzoangelino.aircrowd.flightschedule.spark.FlightScheduleETLProcess;
import org.apache.spark.sql.SparkSession;

public final class FlightSchedule {
    public static void main(String[] args) {
        ConfigFlightScheduleSettings settings = ConfigProvider.getInstance().loadConfig("flight-schedule", ConfigFlightScheduleSettings.class);
        SparkSession session = SparkConfig.getSparkSession();
        SparkETLProcess etl = new FlightScheduleETLProcess(session);
        etl.process(settings.source(), settings.destination());
    }
}
