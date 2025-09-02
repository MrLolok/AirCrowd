package it.lorenzoangelino.aircrowd.airportmanagement.services;

import it.lorenzoangelino.aircrowd.airportmanagement.dto.DashboardData;
import it.lorenzoangelino.aircrowd.airportmanagement.dto.PredictionResponse;
import it.lorenzoangelino.aircrowd.airportmanagement.entities.enums.CheckpointStatus;
import it.lorenzoangelino.aircrowd.airportmanagement.entities.enums.GateStatus;
import it.lorenzoangelino.aircrowd.airportmanagement.repositories.GateRepository;
import it.lorenzoangelino.aircrowd.airportmanagement.repositories.SecurityCheckpointRepository;
import it.lorenzoangelino.aircrowd.airportmanagement.repositories.TerminalRepository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import it.lorenzoangelino.aircrowd.common.models.flights.FlightFlowData;
import it.lorenzoangelino.aircrowd.common.models.weather.WeatherData;
import it.lorenzoangelino.aircrowd.common.models.weather.WeatherDataForecast;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class AirportDataService {

    private final ExternalServiceClient externalServiceClient;
    private final TerminalRepository terminalRepository;
    private final GateRepository gateRepository;
    private final SecurityCheckpointRepository securityCheckpointRepository;

    @Cacheable(value = "dashboard-data", key = "'complete'")
    public DashboardData getCompleteDashboardData() {
        log.info("Fetching complete dashboard data");

        // Chiamate parallele ai servizi esterni
        CompletableFuture<WeatherData> weatherFuture =
                CompletableFuture.supplyAsync(externalServiceClient::getCurrentWeather);

        CompletableFuture<FlightFlowData> flightsFuture =
                CompletableFuture.supplyAsync(externalServiceClient::getTodayFlights);

        CompletableFuture<List<PredictionResponse>> predictionsFuture =
                CompletableFuture.supplyAsync(externalServiceClient::getLatestPredictions);

        // Chiamate locali per status aeroporto
        CompletableFuture<DashboardData.AirportStatusSummary> statusFuture =
                CompletableFuture.supplyAsync(this::getAirportStatusSummary);

        // Attendi completamento di tutti i future
        CompletableFuture.allOf(weatherFuture, flightsFuture, predictionsFuture, statusFuture)
                .join();

        return new DashboardData(
                LocalDateTime.now(),
                weatherFuture.join(),
                flightsFuture.join(),
                predictionsFuture.join(),
                statusFuture.join());
    }

    public FlightFlowData getFlightsForToday() {
        return externalServiceClient.getTodayFlights();
    }

    public FlightFlowData getFlightsForNext24Hours() {
        return externalServiceClient.getFlightsForNext24Hours();
    }

    public WeatherData getCurrentWeather() {
        return externalServiceClient.getCurrentWeather();
    }

    public WeatherDataForecast getWeatherForecast(int days) {
        if (days < 1 || days > 14)
            throw new IllegalArgumentException("Days must be between 1 and 14");
        return externalServiceClient.getWeatherForecast(days);
    }

    public List<PredictionResponse> getLatestPredictions() {
        return externalServiceClient.getLatestPredictions();
    }

    private DashboardData.AirportStatusSummary getAirportStatusSummary() {
        try {
            int totalTerminals = (int) terminalRepository.count();
            int totalGates = (int) gateRepository.count();
            int availableGates =
                    gateRepository.findByStatus(GateStatus.AVAILABLE).size();
            int totalSecurityCheckpoints = (int) securityCheckpointRepository.count();

            int activeSecurityLanes = securityCheckpointRepository.findByStatus(CheckpointStatus.OPERATIONAL).stream()
                    .mapToInt(checkpoint -> checkpoint.getActiveLanes() != null ? checkpoint.getActiveLanes() : 0)
                    .sum();

            String overallStatus = determineOverallStatus(totalGates, availableGates, totalSecurityCheckpoints);

            return new DashboardData.AirportStatusSummary(
                    totalTerminals,
                    totalGates,
                    availableGates,
                    totalSecurityCheckpoints,
                    activeSecurityLanes,
                    overallStatus);

        } catch (Exception e) {
            log.error("Error getting airport status summary", e);
            return new DashboardData.AirportStatusSummary(0, 0, 0, 0, 0, "UNKNOWN");
        }
    }

    private String determineOverallStatus(int totalGates, int availableGates, int totalCheckpoints) {
        if (totalGates == 0 || totalCheckpoints == 0) {
            return "MAINTENANCE";
        }

        double availabilityRate = (double) availableGates / totalGates;

        if (availabilityRate >= 0.8) {
            return "OPERATIONAL";
        } else if (availabilityRate >= 0.5) {
            return "LIMITED";
        } else {
            return "CRITICAL";
        }
    }

    private void validateTimeRange(LocalDateTime from, LocalDateTime to) {
        if (from == null || to == null) {
            throw new IllegalArgumentException("From and to dates cannot be null");
        }
        if (from.isAfter(to)) {
            throw new IllegalArgumentException("Start date must be before end date");
        }
        // Limite ragionevole per evitare query troppo ampie
        if (from.isBefore(LocalDateTime.now().minusDays(90))) {
            throw new IllegalArgumentException("Date range cannot exceed 90 days in the past");
        }
        if (to.isAfter(LocalDateTime.now().plusDays(30))) {
            throw new IllegalArgumentException("Date range cannot exceed 30 days in the future");
        }
    }
}
