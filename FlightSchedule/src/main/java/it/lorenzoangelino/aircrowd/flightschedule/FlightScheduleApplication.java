package it.lorenzoangelino.aircrowd.flightschedule;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication(
        scanBasePackages = {"it.lorenzoangelino.aircrowd.common", "it.lorenzoangelino.aircrowd.flightschedule"})
@ConfigurationPropertiesScan
@EnableCaching
@EnableDiscoveryClient
public class FlightScheduleApplication {

    public static void main(String[] args) {
        SpringApplication.run(FlightScheduleApplication.class, args);
    }
}
