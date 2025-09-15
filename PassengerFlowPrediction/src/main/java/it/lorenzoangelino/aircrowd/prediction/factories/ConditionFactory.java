package it.lorenzoangelino.aircrowd.prediction.factories;

import it.lorenzoangelino.aircrowd.common.models.flights.FlightFlowData;
import it.lorenzoangelino.aircrowd.common.models.weather.WeatherData;
import it.lorenzoangelino.aircrowd.prediction.calculators.FlightFlowCriticalityCalculator;
import it.lorenzoangelino.aircrowd.prediction.calculators.WeatherCriticalityCalculator;
import it.lorenzoangelino.aircrowd.prediction.models.conditions.individual.impl.FlightFlowCondition;
import it.lorenzoangelino.aircrowd.prediction.models.conditions.individual.impl.WeatherCondition;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ConditionFactory {

    private final WeatherCriticalityCalculator weatherCalculator;
    private final FlightFlowCriticalityCalculator flightFlowCalculator;

    public WeatherCondition createWeatherCondition(WeatherData data) {
        return new WeatherCondition(data, weatherCalculator);
    }

    public FlightFlowCondition createFlightFlowCondition(FlightFlowData data) {
        return new FlightFlowCondition(data, flightFlowCalculator);
    }
}