package it.lorenzoangelino.aircrowd.common.models.weather;

import it.lorenzoangelino.aircrowd.common.models.IdentifiableModel;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public record WeatherDataForecast(@NotNull List<WeatherData> hourlyWeatherData) implements IdentifiableModel<String> {
    @Override
    public String getId() {
        if (hourlyWeatherData.isEmpty())
            return "Empty-" + hashCode();
        String firstWeatherDataId = hourlyWeatherData.getFirst().getId();
        String lastWeatherDataId = hourlyWeatherData.getLast().getId();
        return String.format("%s-%s", firstWeatherDataId, lastWeatherDataId);
    }
}
