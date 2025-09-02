package it.lorenzoangelino.aircrowd.airportmanagement.controller;

import it.lorenzoangelino.aircrowd.airportmanagement.dto.*;
import it.lorenzoangelino.aircrowd.airportmanagement.services.AirportDataService;
import java.util.List;

import it.lorenzoangelino.aircrowd.common.models.flights.FlightFlowData;
import it.lorenzoangelino.aircrowd.common.models.weather.WeatherData;
import it.lorenzoangelino.aircrowd.common.models.weather.WeatherDataForecast;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/data")
@RequiredArgsConstructor
@Slf4j
public class AirportDataController {

    private final AirportDataService airportDataService;

    @GetMapping("/dashboard")
    public ResponseEntity<@NotNull APIResponse<DashboardData>> getDashboard() {
        DashboardData data = airportDataService.getCompleteDashboardData();
        return ResponseEntity.ok(APIResponse.success(data, "Dashboard data retrieved successfully"));
    }

    // Flight Data Endpoints
    @GetMapping("/flights/today")
    public ResponseEntity<@NotNull APIResponse<FlightFlowData>> getFlightsForToday() {
        FlightFlowData flights = airportDataService.getFlightsForToday();
        return ResponseEntity.ok(APIResponse.success(flights, "Today's flights retrieved successfully"));
    }

    @GetMapping("/flights/next-24h")
    public ResponseEntity<@NotNull APIResponse<FlightFlowData>> getFlightsForNext24Hours() {
        FlightFlowData flights = airportDataService.getFlightsForNext24Hours();
        return ResponseEntity.ok(APIResponse.success(flights, "Flights for next 24 hours retrieved successfully"));
    }

    // Weather Data Endpoints
    @GetMapping("/weather/current")
    public ResponseEntity<@NotNull APIResponse<WeatherData>> getCurrentWeather() {
        WeatherData weather = airportDataService.getCurrentWeather();
        return ResponseEntity.ok(APIResponse.success(weather, "Current weather retrieved successfully"));
    }

    @GetMapping("/weather/forecast")
    public ResponseEntity<@NotNull APIResponse<WeatherDataForecast>> getWeatherForecast(
            @RequestParam(defaultValue = "7") int days) {

        WeatherDataForecast forecast = airportDataService.getWeatherForecast(days);
        return ResponseEntity.ok(APIResponse.success(
                forecast, String.format("Weather forecast for %d days retrieved successfully", days)));
    }

    // Prediction Data Endpoints
    @GetMapping("/predictions/latest")
    public ResponseEntity<@NotNull APIResponse<List<PredictionResponse>>> getLatestPredictions() {
        List<PredictionResponse> predictions = airportDataService.getLatestPredictions();
        return ResponseEntity.ok(APIResponse.success(predictions, "Latest predictions retrieved successfully"));
    }
}
