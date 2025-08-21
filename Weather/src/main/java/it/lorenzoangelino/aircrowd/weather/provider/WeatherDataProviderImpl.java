package it.lorenzoangelino.aircrowd.weather.provider;

import com.fasterxml.jackson.databind.JsonNode;
import it.lorenzoangelino.aircrowd.common.mapper.Mapper;
import it.lorenzoangelino.aircrowd.common.models.locations.GeographicalLocation;
import it.lorenzoangelino.aircrowd.common.models.weather.WeatherData;
import it.lorenzoangelino.aircrowd.common.models.weather.WeatherDataForecast;
import it.lorenzoangelino.aircrowd.weather.api.clients.APIClientRequester;
import it.lorenzoangelino.aircrowd.weather.exceptions.WeatherServiceException;
import it.lorenzoangelino.aircrowd.weather.retry.RetryPolicy;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;
import lombok.extern.slf4j.Slf4j;
import org.apache.spark.sql.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class WeatherDataProviderImpl implements WeatherDataProvider {
    private final APIClientRequester apiClientRequester;
    private final RetryPolicy retryPolicy;

    public WeatherDataProviderImpl(
            APIClientRequester apiClientRequester, @Autowired(required = false) RetryPolicy retryPolicy) {
        this.apiClientRequester = apiClientRequester;
        this.retryPolicy = retryPolicy != null ? retryPolicy : RetryPolicy.defaultPolicy();
    }

    // Rest of the implementation remains the same...
    @Override
    @Cacheable(cacheNames = "weather-forecast", key = "#location.name + '_' + #startDate + '_' + #endDate")
    public CompletableFuture<WeatherDataForecast> getWeatherDataForecast(
            GeographicalLocation location, LocalDate startDate, LocalDate endDate) {
        log.info(
                "Fetching weather forecast for location: {}, period: {} to {}", location.getName(), startDate, endDate);

        return executeWithRetry(() -> {
            try {
                String endpoint = buildWeatherEndpoint(location, startDate, endDate);

                return apiClientRequester
                        .request(endpoint)
                        .thenApply(this::parseWeatherResponse)
                        .thenApply(WeatherDataForecast::new);

            } catch (Exception e) {
                log.error("Error fetching weather data for location: {}", location.getName(), e);
                throw new WeatherServiceException("Failed to fetch weather data", e);
            }
        });
    }

    @Override
    @Cacheable(cacheNames = "weather-current", key = "#location.name")
    public CompletableFuture<Optional<WeatherData>> getCurrentWeatherData(GeographicalLocation location) {
        return getWeatherDataForecast(location, LocalDate.now(), LocalDate.now())
                .thenApply(forecast -> forecast.getData().stream().findFirst());
    }

    private <T> CompletableFuture<T> executeWithRetry(Supplier<CompletableFuture<T>> operation) {
        return retryPolicy.executeAsync(operation);
    }

    private String buildWeatherEndpoint(GeographicalLocation location, LocalDate startDate, LocalDate endDate) {
        return String.format(
                "/forecast?latitude=%f&longitude=%f&start_date=%s&end_date=%s&hourly=temperature_2m,relative_humidity_2m,wind_speed_10m,weather_code",
                location.getLatitude(), location.getLongitude(), startDate, endDate);
    }

    private List<WeatherData> parseWeatherResponse(String response) {
        try {
            JsonNode jsonNode = Mapper.DEFAULT_MAPPER.readTree(response);
            JsonNode hourlyData = jsonNode.get("hourly");

            if (hourlyData == null) {
                log.warn("No hourly data found in weather response");
                return List.of();
            }

            JsonNode times = hourlyData.get("time");
            JsonNode temperatures = hourlyData.get("temperature_2m");
            JsonNode humidity = hourlyData.get("relative_humidity_2m");
            JsonNode windSpeeds = hourlyData.get("wind_speed_10m");
            JsonNode weatherCodes = hourlyData.get("weather_code");

            List<WeatherData> weatherDataList = new ArrayList<>();
            int dataPoints = times.size();

            for (int i = 0; i < dataPoints; i++) {
                try {
                    WeatherData weatherData = new WeatherData(
                            "weather-" + UUID.randomUUID().toString(),
                            location,
                            parseDateTime(times.get(i).asText()),
                            getValueAtIndex(temperatures, i),
                            getValueAtIndex(humidity, i),
                            getValueAtIndex(windSpeeds, i),
                            parseWeatherConditions(getValueAtIndex(weatherCodes, i)));
                    weatherDataList.add(weatherData);
                } catch (Exception e) {
                    log.warn("Failed to parse weather data at index {}: {}", i, e.getMessage());
                }
            }

            log.info("Successfully parsed {} weather data points", weatherDataList.size());
            return weatherDataList;

        } catch (Exception e) {
            log.error("Failed to parse weather response", e);
            throw new WeatherServiceException("Failed to parse weather API response", e);
        }
    }

    // Helper methods remain the same...
    private LocalDateTime parseDateTime(String timeStr) {
        return LocalDateTime.parse(timeStr);
    }

    private double getValueAtIndex(JsonNode arrayNode, int index) {
        if (arrayNode == null || arrayNode.size() <= index) {
            throw new IndexOutOfBoundsException("Data not available at index " + index);
        }
        return arrayNode.get(index).asDouble();
    }

    private WeatherConditions parseWeatherConditions(double weatherCode) {
        return switch ((int) weatherCode) {
            case 0 -> WeatherConditions.CLEAR;
            case 1, 2, 3 -> WeatherConditions.PARTLY_CLOUDY;
            case 45, 48 -> WeatherConditions.FOG;
            case 51, 53, 55 -> WeatherConditions.DRIZZLE;
            case 61, 63, 65 -> WeatherConditions.RAIN;
            case 71, 73, 75 -> WeatherConditions.SNOW;
            case 95, 96, 99 -> WeatherConditions.THUNDERSTORM;
            default -> WeatherConditions.CLEAR;
        };
    }
}
