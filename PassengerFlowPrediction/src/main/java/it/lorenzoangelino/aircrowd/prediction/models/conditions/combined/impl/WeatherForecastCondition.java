package it.lorenzoangelino.aircrowd.prediction.models.conditions.combined.impl;

import com.fasterxml.jackson.annotation.JsonTypeName;
import it.lorenzoangelino.aircrowd.common.models.weather.WeatherDataForecast;
import it.lorenzoangelino.aircrowd.prediction.models.conditions.combined.CombinedCondition;
import it.lorenzoangelino.aircrowd.prediction.models.conditions.individual.impl.WeatherCondition;
import java.util.List;
import lombok.NoArgsConstructor;
import org.jetbrains.annotations.NotNull;

@JsonTypeName("WeatherForecastCondition")
@NoArgsConstructor
public class WeatherForecastCondition extends SimpleCombinedCondition implements CombinedCondition {
    public WeatherForecastCondition(@NotNull WeatherDataForecast forecast) {
        setConditions(forecast);
    }

    public void setConditions(@NotNull WeatherDataForecast forecast) {
        List<WeatherCondition> conditions = getConditions(forecast);
        super.setConditions(conditions);
    }

    private List<WeatherCondition> getConditions(@NotNull WeatherDataForecast forecast) {
        return forecast.hourlyWeatherData().stream().map(WeatherCondition::new).toList();
    }
}
