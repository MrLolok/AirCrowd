package it.lorenzoangelino.aircrowd.weather.provider.openmeteo;

import com.fasterxml.jackson.databind.ObjectMapper;
import it.lorenzoangelino.aircrowd.common.models.locations.GeographicalLocation;
import it.lorenzoangelino.aircrowd.common.models.weather.WeatherData;
import it.lorenzoangelino.aircrowd.common.models.weather.WeatherDataForecast;
import it.lorenzoangelino.aircrowd.weather.api.callbacks.ResponseCallback;
import it.lorenzoangelino.aircrowd.weather.api.responses.WeatherForecastResponse;
import it.lorenzoangelino.aircrowd.weather.exceptions.WeatherAPIException;
import it.lorenzoangelino.aircrowd.weather.exceptions.WeatherDataProcessingException;
import it.lorenzoangelino.aircrowd.weather.transformer.OpenMeteoResponseTransformer;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeoutException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.hc.client5.http.async.methods.SimpleHttpResponse;

@Slf4j
@RequiredArgsConstructor
public class OpenMeteoCallback implements ResponseCallback {
    private final CompletableFuture<WeatherDataForecast> future;
    private final GeographicalLocation location;
    private final ObjectMapper objectMapper;

    @Override
    public void onSuccess(SimpleHttpResponse response) {
        try {
            String responseBody = response.getBodyText();
            log.debug("Received OpenMeteo response for location: {}", location.name());

            // Parse JSON response using Spring's ObjectMapper
            WeatherForecastResponse weatherResponse =
                    objectMapper.readValue(responseBody, WeatherForecastResponse.class);

            // Transform to our domain models using static utility method
            WeatherDataForecast forecast =
                    OpenMeteoResponseTransformer.transformToWeatherDataForecast(weatherResponse, location);

            future.complete(forecast);

        } catch (Exception e) {
            log.error("Error processing OpenMeteo response for location: {}", location.name(), e);
            future.completeExceptionally(new WeatherDataProcessingException("Failed to process API response", e));
        }
    }

    @Override
    public void onFailure(Exception ex) {
        log.error("OpenMeteo API request failed for location: {}", location.name(), ex);

        if (ex instanceof TimeoutException) {
            future.completeExceptionally(new WeatherAPIException("API request timeout", ex));
        } else {
            future.completeExceptionally(new WeatherAPIException("API request failed", ex));
        }
    }

    // Static factory method for current weather (returns first data point)
    public static OpenMeteoCallback forCurrentWeather(
            CompletableFuture<WeatherData> future, GeographicalLocation location, ObjectMapper objectMapper) {
        CompletableFuture<WeatherDataForecast> forecastFuture = new CompletableFuture<>();

        // Chain the forecast future to extract current weather (first data point)
        forecastFuture
                .thenAccept(forecast -> {
                    try {
                        if (forecast.hourlyWeatherData().isEmpty()) {
                            future.completeExceptionally(new WeatherDataProcessingException(
                                    "No weather data available for current conditions"));
                        } else {
                            // Return the first (most recent) weather data point as current weather
                            WeatherData currentWeather =
                                    forecast.hourlyWeatherData().getFirst();
                            future.complete(currentWeather);
                        }
                    } catch (Exception e) {
                        future.completeExceptionally(e);
                    }
                })
                .exceptionally(throwable -> {
                    future.completeExceptionally(throwable);
                    return null;
                });

        return new OpenMeteoCallback(forecastFuture, location, objectMapper);
    }
}
