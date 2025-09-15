package it.lorenzoangelino.aircrowd.weather;

import it.lorenzoangelino.aircrowd.common.configs.JacksonConfiguration;
import it.lorenzoangelino.aircrowd.common.configs.SparkConfiguration;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;
import org.springframework.kafka.annotation.EnableKafka;

@SpringBootApplication
@ConfigurationPropertiesScan
@EnableKafka
@EnableCaching
@EnableDiscoveryClient
@Import({JacksonConfiguration.class, SparkConfiguration.class})
@ComponentScan(basePackages = {
        "it.lorenzoangelino.aircrowd.weather",
        "it.lorenzoangelino.aircrowd.common.kafka",
        "it.lorenzoangelino.aircrowd.common.spark"
})
public class WeatherApplication {

    public static void main(String[] args) {
        SpringApplication.run(WeatherApplication.class, args);
    }
}
