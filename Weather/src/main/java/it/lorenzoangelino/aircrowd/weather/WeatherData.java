package it.lorenzoangelino.aircrowd.weather;

import java.time.LocalDateTime;

public record WeatherData(
        LocalDateTime time,
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
) {
}
