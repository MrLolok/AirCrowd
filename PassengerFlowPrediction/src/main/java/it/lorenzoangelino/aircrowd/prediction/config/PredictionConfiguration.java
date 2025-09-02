package it.lorenzoangelino.aircrowd.prediction.config;

import it.lorenzoangelino.aircrowd.prediction.providers.FlightFlowConditionProvider;
import it.lorenzoangelino.aircrowd.prediction.providers.FlightFlowConditionProviderImpl;
import it.lorenzoangelino.aircrowd.prediction.providers.WeatherConditionProvider;
import it.lorenzoangelino.aircrowd.prediction.providers.WeatherConditionProviderImpl;
import org.apache.spark.sql.SparkSession;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class PredictionConfiguration {

    @Bean
    public WeatherConditionProvider weatherConditionProvider() {
        return new WeatherConditionProviderImpl("weather-conditions");
    }

    @Bean
    public FlightFlowConditionProvider flightFlowConditionProvider(SparkSession sparkSession) {
        return new FlightFlowConditionProviderImpl(sparkSession);
    }
}
