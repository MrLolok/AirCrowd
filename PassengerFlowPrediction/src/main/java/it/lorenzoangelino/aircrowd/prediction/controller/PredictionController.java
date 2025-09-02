package it.lorenzoangelino.aircrowd.prediction.controller;

import it.lorenzoangelino.aircrowd.common.models.predictions.PassengerFlowPrediction;
import it.lorenzoangelino.aircrowd.prediction.exceptions.PredictionProcessingException;
import it.lorenzoangelino.aircrowd.prediction.services.PassengerFlowPredictionServiceImpl;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/predictions")
@RequiredArgsConstructor
@Slf4j
public class PredictionController {
    
    private final PassengerFlowPredictionServiceImpl predictionService;

    @GetMapping("/latest")
    public ResponseEntity<List<PassengerFlowPrediction>> getLatestPredictions() {
        log.info("Received request for latest predictions");
        
        try {
            List<PassengerFlowPrediction> predictions = generatePredictions();
            
            log.info("Returning {} predictions", predictions.size());
            return ResponseEntity.ok(predictions);
        } catch (Exception e) {
            log.error("Error retrieving latest predictions", e);
            return ResponseEntity.internalServerError().build();
        }
    }

    private List<PassengerFlowPrediction> generatePredictions() {
        List<PassengerFlowPrediction> predictions = new ArrayList<>();
        LocalDateTime now = LocalDateTime.now();
        
        try {
            // Generate predictions for the next 24 hours (every 2 hours)
            for (int i = 0; i < 12; i++) {
                LocalDateTime start = now.plusHours(i * 2);
                LocalDateTime end = start.plusHours(2);
                
                PassengerFlowPrediction prediction = predictionService.predictPassengerFlow(start, end);
                predictions.add(prediction);
            }
        } catch (Exception e) {
            log.error("Error generating predictions using real service", e);
            throw new PredictionProcessingException("Failed to generate predictions", e);
        }
        
        return predictions;
    }
}