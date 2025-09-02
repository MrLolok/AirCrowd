package it.lorenzoangelino.aircrowd.common.models.predictions;

import it.lorenzoangelino.aircrowd.common.models.IdentifiableModel;
import it.lorenzoangelino.aircrowd.common.models.flights.FlightFlowData;
import it.lorenzoangelino.aircrowd.common.models.weather.WeatherDataForecast;
import java.time.LocalDateTime;

public record PassengerFlowPrediction(
        String predictionId,
        LocalDateTime timestamp,
        LocalDateTime predictionTime,
        CriticalityLevel overallCriticality,
        Double criticalityScore,
        Integer predictedPassengerFlow,
        WeatherDataForecast weatherForecast,
        FlightFlowData flightFlow,
        String description
) implements IdentifiableModel<String> {

    @Override
    public String getId() {
        return predictionId;
    }

    public enum CriticalityLevel {
        LOW, MODERATE, MEDIUM, HIGH, SEVERE
    }
}