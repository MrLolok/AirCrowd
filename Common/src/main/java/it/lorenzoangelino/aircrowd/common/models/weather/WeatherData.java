package it.lorenzoangelino.aircrowd.common.models.weather;

import it.lorenzoangelino.aircrowd.common.models.IdentifiableModel;
import it.lorenzoangelino.aircrowd.common.models.locations.GeographicalLocation;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import org.jetbrains.annotations.NotNull;

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
        int windDirection)
        implements IdentifiableModel<String> {
    @Override
    public String getId() {
        return String.format("%s_%s", location.getId(), datetime.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
    }
}
