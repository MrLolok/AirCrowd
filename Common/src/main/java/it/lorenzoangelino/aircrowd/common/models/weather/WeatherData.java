package it.lorenzoangelino.aircrowd.common.models.weather;

import it.lorenzoangelino.aircrowd.common.models.locations.GeographicalLocation;
import it.lorenzoangelino.aircrowd.common.models.IdentifiableModel;
import org.jetbrains.annotations.NotNull;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public record WeatherData(
        @NotNull GeographicalLocation location,
        @NotNull LocalDateTime creation,
        @NotNull LocalDateTime datetime,
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
) implements IdentifiableModel<String> {
    @Override
    public String getId() {
        return String.format("%s_%s", location.getId(), datetime.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
    }
}
