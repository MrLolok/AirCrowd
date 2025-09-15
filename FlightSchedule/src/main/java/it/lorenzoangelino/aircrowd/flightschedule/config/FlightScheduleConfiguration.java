package it.lorenzoangelino.aircrowd.flightschedule.config;

import it.lorenzoangelino.aircrowd.flightschedule.converters.ExtensionConverter;
import it.lorenzoangelino.aircrowd.flightschedule.spark.FlightScheduleExtractor;
import it.lorenzoangelino.aircrowd.flightschedule.spark.FlightScheduleLoader;
import it.lorenzoangelino.aircrowd.flightschedule.spark.FlightScheduleTransformer;
import org.apache.spark.sql.SparkSession;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FlightScheduleConfiguration {

    @Bean
    public FlightScheduleExtractor flightScheduleExtractor(SparkSession sparkSession, @Qualifier("csvConverter") ExtensionConverter converter) {
        return new FlightScheduleExtractor(sparkSession, converter);
    }

    @Bean
    public FlightScheduleTransformer flightScheduleTransformer() {
        return new FlightScheduleTransformer(true, true);
    }

    @Bean
    public FlightScheduleLoader flightScheduleLoader() {
        return new FlightScheduleLoader();
    }
}
