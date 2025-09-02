package it.lorenzoangelino.aircrowd.weather.services;

import it.lorenzoangelino.aircrowd.common.models.locations.GeographicalLocation;
import it.lorenzoangelino.aircrowd.common.models.weather.WeatherData;
import it.lorenzoangelino.aircrowd.common.models.weather.WeatherDataForecast;
import it.lorenzoangelino.aircrowd.weather.provider.WeatherDataProvider;
import it.lorenzoangelino.aircrowd.weather.publisher.WeatherPublisher;
import java.time.LocalDateTime;
import java.util.concurrent.CompletableFuture;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class WeatherServiceImpl implements WeatherService {
    private final WeatherDataProvider weatherDataProvider;
    private final WeatherPublisher weatherPublisher;
    private volatile boolean isPublishingActive = false;

    public WeatherServiceImpl(WeatherDataProvider weatherDataProvider, WeatherPublisher weatherPublisher) {
        this.weatherDataProvider = weatherDataProvider;
        this.weatherPublisher = weatherPublisher;
    }

    @Override
    public WeatherDataProvider getWeatherDataProvider() {
        return weatherDataProvider;
    }

    @Override
    public void startAutomaticPublishing(GeographicalLocation location) {
        if (!isPublishingActive) {
            log.info("Starting automatic weather publishing for location: {}", location.name());
            isPublishingActive = true;
            // Implementation for automatic publishing would go here
        } else {
            log.info("Automatic publishing is already active");
        }
    }

    @Override
    public void stopAutomaticPublishing() {
        if (isPublishingActive) {
            log.info("Stopping automatic weather publishing");
            isPublishingActive = false;
        }
    }

    @Override
    @Cacheable(cacheNames = "weather-forecast", key = "#location.name")
    public CompletableFuture<WeatherDataForecast> getWeatherForecast(GeographicalLocation location) {
        log.info("Getting weather forecast for location: {}", location.name());
        return weatherDataProvider.fetchWeatherDataForecast(location);
    }

    @Override
    @Cacheable(cacheNames = "weather-current", key = "#location.name")
    public CompletableFuture<WeatherData> getCurrentWeather(GeographicalLocation location) {
        log.info("Getting current weather for location: {}", location.name());
        return weatherDataProvider.fetchWeatherData(location, LocalDateTime.now());
    }

    @Override
    @Cacheable(cacheNames = "weather-hourly", key = "#location.name + '_' + #date.toString()")
    public CompletableFuture<WeatherData> getHourlyWeather(GeographicalLocation location, LocalDateTime date) {
        log.info("Getting hourly weather for location: {} at date: {}", location.name(), date);
        return weatherDataProvider.fetchWeatherData(location, date);
    }

    @CacheEvict(
            cacheNames = {"weather-cache", "weather-forecast", "weather-current", "weather-hourly"},
            allEntries = true)
    public void clearCache() {
        log.info("Clearing weather service cache");
    }
}
