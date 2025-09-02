package it.lorenzoangelino.aircrowd.airportmanagement.services;

import it.lorenzoangelino.aircrowd.airportmanagement.clients.FlightServiceClient;
import it.lorenzoangelino.aircrowd.airportmanagement.clients.PredictionServiceClient;
import it.lorenzoangelino.aircrowd.airportmanagement.clients.WeatherServiceClient;
import it.lorenzoangelino.aircrowd.airportmanagement.dto.PredictionResponse;
import feign.FeignException;
import java.util.List;

import it.lorenzoangelino.aircrowd.common.models.flights.FlightFlowData;
import it.lorenzoangelino.aircrowd.common.models.weather.WeatherData;
import it.lorenzoangelino.aircrowd.common.models.weather.WeatherDataForecast;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class ExternalServiceClient {

    private final WeatherServiceClient weatherServiceClient;
    private final FlightServiceClient flightServiceClient;
    private final PredictionServiceClient predictionServiceClient;

    public WeatherData getCurrentWeather() {
        log.debug("Calling weather service for current weather");
        return weatherServiceClient.getCurrentWeather();
    }

    public WeatherDataForecast getWeatherForecast(int days) {
        log.debug("Calling weather service for forecast: {} days", days);
        return weatherServiceClient.getWeatherForecast(days);
    }

    public FlightFlowData getTodayFlights() {
        log.debug("Calling flight service for today's flights");
        return flightServiceClient.getTodayFlights();
    }

    public FlightFlowData getFlightsForNext24Hours() {
        log.debug("Calling flight service for next 24h flights");
        return flightServiceClient.getFlightsForNext24Hours();
    }

    public List<PredictionResponse> getLatestPredictions() {
        try {
            log.debug("Calling prediction service for latest predictions");
            return predictionServiceClient.getLatestPredictions();
        } catch (FeignException e) {
            log.error("Error calling prediction service", e);
            return List.of();
        }
    }
}
