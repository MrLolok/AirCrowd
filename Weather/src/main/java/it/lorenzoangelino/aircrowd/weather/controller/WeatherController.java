package it.lorenzoangelino.aircrowd.weather.controller;

import it.lorenzoangelino.aircrowd.common.models.locations.GeographicalLocation;
import it.lorenzoangelino.aircrowd.common.models.weather.WeatherData;
import it.lorenzoangelino.aircrowd.common.models.weather.WeatherDataForecast;
import it.lorenzoangelino.aircrowd.weather.config.WeatherConfigurationProperties;
import it.lorenzoangelino.aircrowd.weather.services.WeatherService;
import java.util.concurrent.ExecutionException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/weather")
@RequiredArgsConstructor
@Slf4j
public class WeatherController {

    private final WeatherService weatherService;
    private final WeatherConfigurationProperties weatherConfig;

    @GetMapping("/current")
    public WeatherData getCurrentWeather() {
        log.info("Received request for current weather");

        try {
            GeographicalLocation location = new GeographicalLocation(
                    weatherConfig.location().name(),
                    weatherConfig.location().latitude(),
                    weatherConfig.location().longitude());

            WeatherData weatherData = weatherService.getCurrentWeather(location).get();

            log.info(
                    "Returning current weather data: temp={}, humidity={}",
                    weatherData.temperature(),
                    weatherData.humidity());

            return weatherData;

        } catch (InterruptedException | ExecutionException e) {
            log.error("Error retrieving current weather", e);
            throw new RuntimeException("Failed to retrieve current weather", e);
        }
    }

    @GetMapping("/forecast")
    public WeatherDataForecast getWeatherForecast(@RequestParam("days") int days) {
        log.info("Received request for weather forecast: {} days", days);

        try {
            GeographicalLocation location = new GeographicalLocation(
                    weatherConfig.location().name(),
                    weatherConfig.location().latitude(),
                    weatherConfig.location().longitude());

            WeatherDataForecast forecastData =
                    weatherService.getWeatherForecast(location).get();

            // Filter forecast data to requested number of days (assuming hourly data)
            int maxDataPoints = days * 24;
            if (forecastData.hourlyWeatherData().size() > maxDataPoints) {
                var limitedData = forecastData.hourlyWeatherData().subList(0, maxDataPoints);
                forecastData = new WeatherDataForecast(limitedData);
            }

            log.info(
                    "Returning weather forecast for {} days with {} entries",
                    days,
                    forecastData.hourlyWeatherData().size());

            return forecastData;

        } catch (InterruptedException | ExecutionException e) {
            log.error("Error retrieving weather forecast", e);
            throw new RuntimeException("Failed to retrieve weather forecast", e);
        }
    }
}
