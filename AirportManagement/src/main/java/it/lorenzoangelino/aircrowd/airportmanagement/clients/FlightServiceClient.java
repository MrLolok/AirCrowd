package it.lorenzoangelino.aircrowd.airportmanagement.clients;

import it.lorenzoangelino.aircrowd.common.models.flights.FlightFlowData;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

@FeignClient(name = "flight-service", url = "${aircrowd.services.flight.url:http://flight-service}")
public interface FlightServiceClient {

    @GetMapping("/api/v1/flights/today")
    FlightFlowData getTodayFlights();

    @GetMapping("/api/v1/flights/next-24h")
    FlightFlowData getFlightsForNext24Hours();
}