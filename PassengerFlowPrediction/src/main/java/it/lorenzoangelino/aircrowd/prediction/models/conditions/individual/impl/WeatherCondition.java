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
    private final static Calculator<WeatherData, Float> WEATHER_CRITICALITY_CALCULATOR = new WeatherCriticalityCalculator();

    public WeatherCondition(WeatherData data) {
        super(data);
    }

    @Override
    protected Calculator<WeatherData, Float> getCriticalityCalculator() {
        return WEATHER_CRITICALITY_CALCULATOR;
    }
}
