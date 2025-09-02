package it.lorenzoangelino.aircrowd.flightschedule.controller;

import it.lorenzoangelino.aircrowd.common.models.flights.FlightData;
import it.lorenzoangelino.aircrowd.common.models.flights.FlightFlowData;
import it.lorenzoangelino.aircrowd.common.models.flights.enums.FlightType;
import java.time.LocalDateTime;
import java.util.List;
import java.util.ArrayList;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/flights")
@RequiredArgsConstructor
@Slf4j
public class FlightController {

    @GetMapping("/today")
    public ResponseEntity<FlightFlowData> getTodayFlights() {
        log.info("Received request for today's flights");
        
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime startOfDay = now.toLocalDate().atStartOfDay();
        LocalDateTime endOfDay = now.toLocalDate().atTime(23, 59, 59);
        
        List<FlightData> flights = generateMockFlights(startOfDay, endOfDay);
        FlightFlowData response = new FlightFlowData(flights);
        
        log.info("Returning {} flights for today", flights.size());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/next-24h")
    public ResponseEntity<FlightFlowData> getFlightsForNext24Hours() {
        log.info("Received request for flights in next 24 hours");
        
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime next24h = now.plusHours(24);
        
        List<FlightData> flights = generateMockFlights(now, next24h);
        FlightFlowData response = new FlightFlowData(flights);
        
        log.info("Returning {} flights for next 24 hours", flights.size());
        return ResponseEntity.ok(response);
    }

    private List<FlightData> generateMockFlights(LocalDateTime start, LocalDateTime end) {
        List<FlightData> flights = new ArrayList<>();
        
        String[] airlines = {"AZ", "LH", "AF", "BA", "KL", "LX", "OS", "IB", "EI", "SK"};
        
        LocalDateTime currentTime = start;
        int flightNumber = 100;
        
        while (currentTime.isBefore(end)) {
            // Create arrival flight
            flights.add(new FlightData(
                airlines[(int)(Math.random() * airlines.length)] + flightNumber,
                FlightType.ARRIVAL,
                currentTime,
                (short) (150 + (int)(Math.random() * 200))
            ));
            
            // Create departure flight
            flights.add(new FlightData(
                airlines[(int)(Math.random() * airlines.length)] + (flightNumber + 1000),
                FlightType.DEPARTURE,
                currentTime.plusMinutes(30),
                (short) (150 + (int)(Math.random() * 200))
            ));
            
            currentTime = currentTime.plusHours(2);
            flightNumber++;
        }
        
        return flights;
    }
}