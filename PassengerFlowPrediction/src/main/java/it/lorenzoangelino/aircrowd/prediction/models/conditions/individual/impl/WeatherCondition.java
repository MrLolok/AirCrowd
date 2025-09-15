package it.lorenzoangelino.aircrowd.prediction.models.conditions.individual.impl;

import com.fasterxml.jackson.annotation.JsonTypeName;
import it.lorenzoangelino.aircrowd.common.models.weather.WeatherData;
import it.lorenzoangelino.aircrowd.prediction.calculators.Calculator;
import it.lorenzoangelino.aircrowd.prediction.calculators.WeatherCriticalityCalculator;
import it.lorenzoangelino.aircrowd.prediction.models.conditions.individual.AbstractIndividualCondition;
import lombok.Getter;

@JsonTypeName("WeatherCondition")
@Getter
public class WeatherCondition extends AbstractIndividualCondition<WeatherData> {

    private final Calculator<WeatherData, Float> weatherCriticalityCalculator;

    public WeatherCondition(WeatherData data, WeatherCriticalityCalculator calculator) {
        super(data);
        this.weatherCriticalityCalculator = calculator;
    }

    @Override
    protected Calculator<WeatherData, Float> getCriticalityCalculator() {
        return weatherCriticalityCalculator;
    }
}
