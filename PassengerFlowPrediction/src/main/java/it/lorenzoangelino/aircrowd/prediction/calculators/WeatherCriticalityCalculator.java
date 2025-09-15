package it.lorenzoangelino.aircrowd.prediction.calculators;

import it.lorenzoangelino.aircrowd.common.models.weather.WeatherData;
import it.lorenzoangelino.aircrowd.prediction.configs.ConfigCriticalitySettings;
import it.lorenzoangelino.aircrowd.prediction.services.CriticalityConfigurationService;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class WeatherCriticalityCalculator extends AbstractCriticalityCalculator<WeatherData> {

    private final CriticalityConfigurationService configService;

    @Override
    protected @NotNull ConfigCriticalitySettings getCriticalitySettings() {
        return configService.getWeatherCriticalitySettings();
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
