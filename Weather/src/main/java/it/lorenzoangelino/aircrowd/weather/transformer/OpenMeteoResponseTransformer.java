package it.lorenzoangelino.aircrowd.weather.transformer;

import it.lorenzoangelino.aircrowd.common.models.locations.GeographicalLocation;
import it.lorenzoangelino.aircrowd.common.models.weather.WeatherData;
import it.lorenzoangelino.aircrowd.common.models.weather.WeatherDataForecast;
import it.lorenzoangelino.aircrowd.weather.api.responses.WeatherForecastResponse;
import it.lorenzoangelino.aircrowd.weather.exceptions.WeatherDataProcessingException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@UtilityClass
public class OpenMeteoResponseTransformer {

    private static final DateTimeFormatter OPENMETEO_DATETIME_FORMAT =
            DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm");

    public static WeatherDataForecast transformToWeatherDataForecast(
            WeatherForecastResponse response, GeographicalLocation location) {

        if (response == null) {
            throw new WeatherDataProcessingException("WeatherForecastResponse cannot be null");
        }

        if (response.hourly() == null) {
            throw new WeatherDataProcessingException("Hourly data is missing from OpenMeteo response");
        }

        var hourlyData = response.hourly();
        validateHourlyData(hourlyData);

        List<WeatherData> weatherDataList = new ArrayList<>();
        LocalDateTime creationTime = LocalDateTime.now();

        try {
            int dataSize = hourlyData.time().size();

            for (int i = 0; i < dataSize; i++) {
                WeatherData weatherData = createWeatherDataFromIndex(hourlyData, location, creationTime, i);
                weatherDataList.add(weatherData);
            }

            log.debug("Successfully transformed {} hourly weather data points", weatherDataList.size());
            return new WeatherDataForecast(weatherDataList);

        } catch (Exception e) {
            log.error("Error transforming OpenMeteo response to WeatherDataForecast", e);
            throw new WeatherDataProcessingException("Failed to transform OpenMeteo response", e);
        }
    }

    public static WeatherData transformToCurrentWeatherData(
            WeatherForecastResponse response, GeographicalLocation location) {

        WeatherDataForecast forecast = transformToWeatherDataForecast(response, location);

        if (forecast.hourlyWeatherData().isEmpty()) {
            throw new WeatherDataProcessingException("No weather data available for current conditions");
        }

        // Return the first (most recent) weather data point as current weather
        return forecast.hourlyWeatherData().getFirst();
    }

    private static void validateHourlyData(WeatherForecastResponse.HourlyData hourlyData) {
        if (hourlyData.time() == null || hourlyData.time().isEmpty()) {
            throw new WeatherDataProcessingException("Time data is missing from hourly weather data");
        }

        int expectedSize = hourlyData.time().size();

        if (hourlyData.temperature() == null || hourlyData.temperature().size() != expectedSize) {
            throw new WeatherDataProcessingException("Temperature data is inconsistent");
        }

        if (hourlyData.relativeHumidity() == null
                || hourlyData.relativeHumidity().size() != expectedSize) {
            throw new WeatherDataProcessingException("Humidity data is inconsistent");
        }

        log.debug("Hourly data validation passed for {} data points", expectedSize);
    }

    private static WeatherData createWeatherDataFromIndex(
            WeatherForecastResponse.HourlyData hourlyData,
            GeographicalLocation location,
            LocalDateTime creationTime,
            int index) {

        try {
            LocalDateTime datetime = LocalDateTime.parse(hourlyData.time().get(index), OPENMETEO_DATETIME_FORMAT);

            double temperature = getValueOrDefault(hourlyData.temperature(), index, 0.0);
            int humidity = getValueOrDefault(hourlyData.relativeHumidity(), index, 50);
            double dewPoint = getValueOrDefault(hourlyData.dewPoint(), index, 0.0);
            int precipitationProbability = getValueOrDefault(hourlyData.precipitationProbability(), index, 0);
            double rain = getValueOrDefault(hourlyData.rain(), index, 0.0);
            double showers = getValueOrDefault(hourlyData.showers(), index, 0.0);
            double snowfall = getValueOrDefault(hourlyData.snowfall(), index, 0.0);
            double pressure = getValueOrDefault(hourlyData.pressure(), index, 1013.25);
            int cloudCover = getValueOrDefault(hourlyData.cloudCover(), index, 0);
            int visibility = getValueOrDefault(hourlyData.visibility(), index, 10000);
            double windSpeed = getValueOrDefault(hourlyData.windSpeed(), index, 0.0);
            int windDirection = getValueOrDefault(hourlyData.windDirection(), index, 0);

            return new WeatherData(
                    location,
                    creationTime,
                    datetime,
                    temperature,
                    humidity,
                    dewPoint,
                    precipitationProbability,
                    rain,
                    showers,
                    snowfall,
                    pressure,
                    cloudCover,
                    visibility,
                    windSpeed,
                    windDirection);

        } catch (DateTimeParseException e) {
            throw new WeatherDataProcessingException(
                    "Failed to parse datetime: " + hourlyData.time().get(index), e);
        } catch (IndexOutOfBoundsException e) {
            throw new WeatherDataProcessingException("Data index " + index + " is out of bounds", e);
        }
    }

    private static <T> T getValueOrDefault(List<T> list, int index, T defaultValue) {
        if (list == null || index >= list.size() || list.get(index) == null) {
            log.debug("Using default value {} for index {}", defaultValue, index);
            return defaultValue;
        }
        return list.get(index);
    }
}
