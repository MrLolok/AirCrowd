package it.lorenzoangelino.aircrowd.airportmanagement.dto;

import it.lorenzoangelino.aircrowd.common.models.flights.FlightFlowData;
import it.lorenzoangelino.aircrowd.common.models.weather.WeatherData;

import java.time.LocalDateTime;
import java.util.List;

public record DashboardData(
        LocalDateTime timestamp,
        WeatherData currentWeather,
        FlightFlowData todaysFlights,
        List<PredictionResponse> latestPredictions,
        AirportStatusSummary airportStatus) {

    public record AirportStatusSummary(
            int totalTerminals,
            int totalGates,
            int availableGates,
            int totalSecurityCheckpoints,
            int activeSecurityLanes,
            String overallStatus) {}
}
