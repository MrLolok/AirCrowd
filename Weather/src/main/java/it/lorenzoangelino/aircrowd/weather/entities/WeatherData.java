package it.lorenzoangelino.aircrowd.weather.entities;

import org.jetbrains.annotations.NotNull;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public record WeatherData(
        @NotNull WeatherLocation location,
        @NotNull LocalDateTime time,
        double temperature,
        int humidity,
        double dewPoint,
        int precipitationProbability,
        double rain,
        double showers,
        double snowfall,
        double pressure,
        int cloudCover,
        int visibility,
        double windSpeed,
        int windDirection
) implements IdentifiableEntity<String> {
    @Override
    public String getId() {
        return String.format("%s_%s", location.getId(), time.format(DateTimeFormatter.ISO_INSTANT));
    }
}
