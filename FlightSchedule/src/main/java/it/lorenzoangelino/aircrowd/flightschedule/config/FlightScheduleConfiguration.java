package it.lorenzoangelino.aircrowd.flightschedule.config;

import it.lorenzoangelino.aircrowd.flightschedule.converters.CSVConverter;
import it.lorenzoangelino.aircrowd.flightschedule.readers.XLSXFileReader;
import it.lorenzoangelino.aircrowd.flightschedule.spark.FlightScheduleExtractor;
import it.lorenzoangelino.aircrowd.flightschedule.spark.FlightScheduleLoader;
import it.lorenzoangelino.aircrowd.flightschedule.spark.FlightScheduleTransformer;
import org.apache.spark.sql.SparkSession;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FlightScheduleConfiguration {

    @Bean
    public CSVConverter csvConverter() {
        return new CSVConverter();
    }

    @Bean
    public XLSXFileReader xlsxFileReader() {
        return new XLSXFileReader();
    }

    @Bean
    public FlightScheduleExtractor flightScheduleExtractor(SparkSession sparkSession) {
        return new FlightScheduleExtractor(sparkSession);
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
