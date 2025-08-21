package it.lorenzoangelino.aircrowd.prediction.calculators;

import it.lorenzoangelino.aircrowd.common.configs.ConfigProvider;
import it.lorenzoangelino.aircrowd.common.models.weather.WeatherData;
import it.lorenzoangelino.aircrowd.prediction.configs.ConfigCriticalitySettings;
import org.jetbrains.annotations.NotNull;

public class WeatherCriticalityCalculator extends AbstractCriticalityCalculator<WeatherData> {
    private static final ConfigCriticalitySettings WEATHER_CRITICALITY_CONFIG =
            ConfigProvider.getInstance().loadConfig("weather-criticality", ConfigCriticalitySettings.class);

    @Override
    protected @NotNull ConfigCriticalitySettings getCriticalitySettings() {
        return WEATHER_CRITICALITY_CONFIG;
    }

    @Override
    protected @NotNull Double[] getNumericValues(WeatherData input) {
        return new Double[] {
            input.temperature(),
            (double) input.humidity(),
            input.rain(),
            input.showers(),
            input.snowfall(),
            (double) input.cloudCover(),
            (double) input.visibility(),
            input.windSpeed()
        };
    }
}
