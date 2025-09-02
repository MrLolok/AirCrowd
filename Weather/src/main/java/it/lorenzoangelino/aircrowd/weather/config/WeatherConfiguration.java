package it.lorenzoangelino.aircrowd.weather.config;

import it.lorenzoangelino.aircrowd.weather.api.clients.APIClientRequester;
import it.lorenzoangelino.aircrowd.weather.api.clients.HttpAPIClientRequester;
import it.lorenzoangelino.aircrowd.weather.retry.RetryPolicy;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class WeatherConfiguration {

    @Bean
    public APIClientRequester apiClientRequester() {
        return new HttpAPIClientRequester();
    }

    @Bean
    public RetryPolicy retryPolicy() {
        return new RetryPolicy();
    }
}
