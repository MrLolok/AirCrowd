package it.lorenzoangelino.aircrowd.prediction.services;

import it.lorenzoangelino.aircrowd.prediction.providers.ConditionProvider;
import it.lorenzoangelino.aircrowd.prediction.providers.FlightFlowConditionProvider;
import it.lorenzoangelino.aircrowd.prediction.providers.WeatherConditionProvider;

public interface PassengerFlowPredictionService extends ConditionProvider {
    WeatherConditionProvider getWeatherConditionProvider();

    FlightFlowConditionProvider getFlightFlowConditionProvider();
}
