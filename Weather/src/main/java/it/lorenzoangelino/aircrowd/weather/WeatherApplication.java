package it.lorenzoangelino.aircrowd.weather;

import it.lorenzoangelino.aircrowd.weather.config.WeatherConfigurationProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.kafka.annotation.EnableKafka;

@SpringBootApplication(scanBasePackages = {"it.lorenzoangelino.aircrowd.common", "it.lorenzoangelino.aircrowd.weather"})
@ConfigurationPropertiesScan
@EnableConfigurationProperties(WeatherConfigurationProperties.class)
@EnableKafka
@EnableCaching
@EnableDiscoveryClient
public class WeatherApplication {

    public static void main(String[] args) {
        SpringApplication.run(WeatherApplication.class, args);
    }
}
