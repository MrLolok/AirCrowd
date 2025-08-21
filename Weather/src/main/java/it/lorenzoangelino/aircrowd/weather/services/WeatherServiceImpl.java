package it.lorenzoangelino.aircrowd.weather.services;

import it.lorenzoangelino.aircrowd.weather.provider.WeatherDataProvider;
import it.lorenzoangelino.aircrowd.weather.publisher.WeatherPublisher;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@Cacheable(cacheNames = "weather-cache")
public class WeatherServiceImpl implements WeatherService {
    private final WeatherDataProvider weatherDataProvider;
    private final WeatherPublisher weatherPublisher;

    public WeatherServiceImpl(WeatherDataProvider weatherDataProvider, WeatherPublisher weatherPublisher) {
        this.weatherDataProvider = weatherDataProvider;
        this.weatherPublisher = weatherPublisher;
    }

    @Override
    public void execute() {
        try {
            LOGGER.info("Starting Weather Service execution");
            weatherPublisher.start();
            LOGGER.info("Weather Service started successfully");
        } catch (Exception e) {
            LOGGER.error("Failed to start Weather Service", e);
            throw new RuntimeException("Weather Service execution failed", e);
        }
    }

    @Override
    @CacheEvict(cacheNames = "weather-cache", allEntries = true)
    public void shutdown() {
        try {
            LOGGER.info("Shutting down Weather Service");
            weatherPublisher.shutdown();
        } catch (Exception e) {
            LOGGER.error("Error during Weather Service shutdown", e);
        }
    }

    @Override
    public WeatherDataProvider getWeatherDataProvider() {
        return weatherDataProvider;
    }
}
