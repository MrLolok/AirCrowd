package it.lorenzoangelino.aircrowd.airportmanagement;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@EnableCaching
@EnableFeignClients
@ComponentScan(basePackages = {"it.lorenzoangelino.aircrowd.airportmanagement", "it.lorenzoangelino.aircrowd.common"})
public class AirportManagementApplication {

    public static void main(String[] args) {
        SpringApplication.run(AirportManagementApplication.class, args);
    }
}
