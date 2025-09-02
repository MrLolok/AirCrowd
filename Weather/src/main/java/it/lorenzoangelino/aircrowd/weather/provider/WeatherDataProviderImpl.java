package it.lorenzoangelino.aircrowd.weather.provider;

import com.fasterxml.jackson.databind.ObjectMapper;
import it.lorenzoangelino.aircrowd.common.models.locations.GeographicalLocation;
import it.lorenzoangelino.aircrowd.common.models.weather.WeatherData;
import it.lorenzoangelino.aircrowd.common.models.weather.WeatherDataForecast;
import it.lorenzoangelino.aircrowd.weather.api.clients.HttpAPIClientRequester;
import it.lorenzoangelino.aircrowd.weather.api.params.QueryParam;
import it.lorenzoangelino.aircrowd.weather.config.WeatherConfigurationProperties;
import it.lorenzoangelino.aircrowd.weather.exceptions.WeatherAPIException;
import it.lorenzoangelino.aircrowd.weather.provider.openmeteo.OpenMeteoCallback;
import it.lorenzoangelino.aircrowd.weather.retry.RetryPolicy;
import java.time.LocalDateTime;
import java.util.concurrent.CompletableFuture;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class WeatherDataProviderImpl implements WeatherDataProvider {

    private final WeatherConfigurationProperties weatherConfig;
    private final HttpAPIClientRequester httpClient;
    private final RetryPolicy retryPolicy;
    private final ObjectMapper objectMapper;

    @Override
    @Cacheable(cacheNames = "weather-forecast", key = "#location.name")
    public CompletableFuture<WeatherDataForecast> fetchWeatherDataForecast(GeographicalLocation location) {
        log.info("Fetching weather forecast for location: {}", location.name());

        return retryPolicy
                .execute(() -> fetchOpenMeteoForecast(location), "fetchWeatherDataForecast")
                .handle((forecast, throwable) -> {
                    if (throwable != null) {
                        log.error("Error fetching weather forecast for location: {}", location.name(), throwable);
                        throw new WeatherAPIException("Failed to fetch weather forecast", throwable);
                    }
                    return forecast;
                });
    }

    @Override
    @Cacheable(cacheNames = "weather-current", key = "#location.name + '_' + #date.toString()")
    public CompletableFuture<WeatherData> fetchWeatherData(GeographicalLocation location, LocalDateTime date) {
        log.info("Fetching weather data for location: {} at date: {}", location.name(), date);

        return retryPolicy
                .execute(() -> fetchOpenMeteoCurrentWeather(location), "fetchWeatherData")
                .handle((weatherData, throwable) -> {
                    if (throwable != null) {
                        log.error(
                                "Error fetching weather data for location: {} at date: {}",
                                location.name(),
                                date,
                                throwable);
                        throw new WeatherAPIException("Failed to fetch weather data", throwable);
                    }
                    return weatherData;
                });
    }

    private CompletableFuture<WeatherDataForecast> fetchOpenMeteoForecast(GeographicalLocation location) {
        CompletableFuture<WeatherDataForecast> future = new CompletableFuture<>();

        try {
            // Set up API client
            httpClient.setBaseURL(weatherConfig.openmeteo().baseUrl());

            // Prepare query parameters for OpenMeteo API
            QueryParam[] params = {
                new QueryParam("latitude", String.valueOf(location.latitude())),
                new QueryParam("longitude", String.valueOf(location.longitude())),
                new QueryParam("hourly", weatherConfig.openmeteo().hourly()),
                new QueryParam(
                        "forecast_days",
                        String.valueOf(weatherConfig.openmeteo().forecastDays())),
                new QueryParam("timezone", weatherConfig.openmeteo().timezone())
            };

            // Make API request
            httpClient.get(new OpenMeteoCallback(future, location, objectMapper), "/forecast", params);

        } catch (Exception e) {
            log.error("Error setting up OpenMeteo API request for location: {}", location.name(), e);
            future.completeExceptionally(new WeatherAPIException("Failed to setup API request", e));
        }

        return future;
    }

    private CompletableFuture<WeatherData> fetchOpenMeteoCurrentWeather(GeographicalLocation location) {
        CompletableFuture<WeatherData> future = new CompletableFuture<>();

        try {
            // Set up API client
            httpClient.setBaseURL(weatherConfig.openmeteo().baseUrl());

            // Prepare query parameters for OpenMeteo API (only current data, 1 day)
            QueryParam[] params = {
                new QueryParam("latitude", String.valueOf(location.latitude())),
                new QueryParam("longitude", String.valueOf(location.longitude())),
                new QueryParam("hourly", weatherConfig.openmeteo().hourly()),
                new QueryParam("forecast_days", "1"),
                new QueryParam("timezone", weatherConfig.openmeteo().timezone())
            };

            // Make API request using factory method for current weather
            httpClient.get(OpenMeteoCallback.forCurrentWeather(future, location, objectMapper), "/forecast", params);

        } catch (Exception e) {
            log.error("Error setting up OpenMeteo API request for location: {}", location.name(), e);
            future.completeExceptionally(new WeatherAPIException("Failed to setup API request", e));
        }

        return future;
    }
}
