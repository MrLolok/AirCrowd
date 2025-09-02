package it.lorenzoangelino.aircrowd.prediction.services;

import it.lorenzoangelino.aircrowd.common.models.predictions.PassengerFlowPrediction;
import it.lorenzoangelino.aircrowd.prediction.exceptions.PredictionProcessingException;
import it.lorenzoangelino.aircrowd.prediction.models.conditions.Condition;
import it.lorenzoangelino.aircrowd.prediction.models.conditions.combined.impl.SimpleCombinedCondition;
import it.lorenzoangelino.aircrowd.prediction.models.criticality.CriticalityScore;
import it.lorenzoangelino.aircrowd.prediction.providers.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class PassengerFlowPredictionServiceImpl implements PassengerFlowPredictionService {
    private final WeatherConditionProvider weatherConditionProvider;
    private final FlightFlowConditionProvider flightFlowConditionProvider;

    @Override
    public Optional<Condition> getCondition(LocalDateTime start, LocalDateTime end) {
        log.info("Getting condition for period: {} to {}", start, end);
        
        try {
            Optional<Condition> weatherCondition = weatherConditionProvider.getCondition(start, end);
            Optional<Condition> flightCondition = flightFlowConditionProvider.getCondition(start, end);
            
            if (weatherCondition.isPresent() && flightCondition.isPresent()) {
                Condition combined = new SimpleCombinedCondition(List.of(weatherCondition.get(), flightCondition.get()));
                return Optional.of(combined);
            }
            
            return Optional.empty();
        } catch (Exception e) {
            log.error("Failed to get condition for period: {} to {}", start, end, e);
            throw new PredictionProcessingException("Failed to get prediction condition", e);
        }
    }
    
    @Override
    public WeatherConditionProvider getWeatherConditionProvider() {
        return weatherConditionProvider;
    }
    
    @Override
    public FlightFlowConditionProvider getFlightFlowConditionProvider() {
        return flightFlowConditionProvider;
    }
    
    @Cacheable(cacheNames = "prediction-results", key = "#start + '_' + #end")
    public PassengerFlowPrediction predictPassengerFlow(LocalDateTime start, LocalDateTime end) {
        log.info("Predicting passenger flow for period: {} to {}", start, end);
        
        try {
            Optional<Condition> condition = getCondition(start, end);
            
            if (condition.isEmpty()) {
                throw new PredictionProcessingException("No conditions available for prediction period");
            }
            
            CriticalityScore criticalityScore = condition.get().calculateCriticality();
            
            // Calculate predicted passenger flow based on criticality
            int predictedFlow = calculatePassengerFlow(criticalityScore);
            
            String predictionId = java.util.UUID.randomUUID().toString();
            String description = String.format("Passenger flow prediction for %s - %s criticality", 
                end.toLocalTime(), criticalityScore.getLevel().name().toLowerCase());
            
            PassengerFlowPrediction prediction = new PassengerFlowPrediction(
                predictionId,
                LocalDateTime.now(),
                end,
                criticalityScore.getLevel(),
                (double) criticalityScore.getValue(),
                predictedFlow,
                null, // Weather forecast would come from weather service
                null, // Flight flow would come from flight service  
                description
            );
            
            log.info("Generated prediction with flow: {} passengers, criticality: {}", 
                predictedFlow, criticalityScore.getLevel());
            
            return prediction;
            
        } catch (Exception e) {
            log.error("Failed to predict passenger flow for period: {} to {}", start, end, e);
            throw new PredictionProcessingException("Passenger flow prediction failed", e);
        }
    }
    
    private int calculatePassengerFlow(CriticalityScore score) {
        // Base passenger flow calculation based on criticality score
        float normalizedScore = Math.min(1.0f, Math.max(0.0f, score.getValue()));
        return (int) (500 + (normalizedScore * 2000)); // Range: 500-2500 passengers
    }
}
