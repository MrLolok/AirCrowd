package it.lorenzoangelino.aircrowd.weather.api.responses;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

public record WeatherForecastResponse(
        double latitude,
        double longitude,
        double elevation,
        String timezone,
        @JsonProperty("timezone_abbreviation") String timezoneAbbreviation,
        @JsonProperty("generationtime_ms") double generationTime,
        @JsonProperty("utc_offset_seconds") int utcOffsetSeconds,
        @JsonProperty("hourly_units") HourlyUnits hourlyUnits,
        HourlyData hourly) {
    public record HourlyUnits(
            String time,
            @JsonProperty("temperature_2m") String temperature,
            @JsonProperty("relative_humidity_2m") String relativeHumidity2m,
            @JsonProperty("dew_point_2m") String dewPoint2m,
            @JsonProperty("precipitation_probability") String precipitationProbability,
            String rain,
            String showers,
            String snowfall,
            @JsonProperty("pressure_msl") String pressure,
            @JsonProperty("cloud_cover") String cloudCover,
            String visibility,
            @JsonProperty("wind_speed_10m") String windSpeed,
            @JsonProperty("wind_direction_10m") String windDirection) {}

    public record HourlyData(
            List<String> time,
            @JsonProperty("temperature_2m") List<Double> temperature,
            @JsonProperty("relative_humidity_2m") List<Integer> relativeHumidity,
            @JsonProperty("dew_point_2m") List<Double> dewPoint,
            @JsonProperty("precipitation_probability") List<Integer> precipitationProbability,
            List<Double> rain,
            List<Double> showers,
            List<Double> snowfall,
            @JsonProperty("pressure_msl") List<Double> pressure,
            @JsonProperty("cloud_cover") List<Integer> cloudCover,
            List<Integer> visibility,
            @JsonProperty("wind_speed_10m") List<Double> windSpeed,
            @JsonProperty("wind_direction_10m") List<Integer> windDirection) {}
}
