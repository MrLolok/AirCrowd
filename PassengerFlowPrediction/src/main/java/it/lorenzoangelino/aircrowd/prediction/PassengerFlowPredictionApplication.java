package it.lorenzoangelino.aircrowd.prediction;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.kafka.annotation.EnableKafka;

@SpringBootApplication(
        scanBasePackages = {"it.lorenzoangelino.aircrowd.common", "it.lorenzoangelino.aircrowd.prediction"})
@ConfigurationPropertiesScan
@EnableKafka
@EnableCaching
@EnableEurekaClient
public class PassengerFlowPredictionApplication {

    public static void main(String[] args) {
        SpringApplication.run(PassengerFlowPredictionApplication.class, args);
    }
}
