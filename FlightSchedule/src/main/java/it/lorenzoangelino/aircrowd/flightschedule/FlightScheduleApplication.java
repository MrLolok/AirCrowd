package it.lorenzoangelino.aircrowd.flightschedule;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication(
        scanBasePackages = {"it.lorenzoangelino.aircrowd.common", "it.lorenzoangelino.aircrowd.flightschedule"})
@ConfigurationPropertiesScan
@EnableCaching
public class FlightScheduleApplication {

    public static void main(String[] args) {
        SpringApplication.run(FlightScheduleApplication.class, args);
    }
}
