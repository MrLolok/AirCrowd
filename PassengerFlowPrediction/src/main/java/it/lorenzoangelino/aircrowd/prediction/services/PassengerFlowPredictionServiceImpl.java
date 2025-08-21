package it.lorenzoangelino.aircrowd.prediction.services;

import it.lorenzoangelino.aircrowd.prediction.models.conditions.Condition;
import it.lorenzoangelino.aircrowd.prediction.models.conditions.combined.impl.SimpleCombinedCondition;
import it.lorenzoangelino.aircrowd.prediction.providers.*;

import java.util.List;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@Cacheable(cacheNames = "prediction-cache")
public class PassengerFlowPredictionServiceImpl implements PassengerFlowPredictionService {
    private final WeatherConditionProvider weatherConditionProvider;
    private final FlightFlowConditionProvider flightFlowConditionProvider;

    public PassengerFlowPredictionServiceImpl(
            WeatherConditionProvider weatherConditionProvider,
            FlightFlowConditionProvider flightFlowConditionProvider) {
        this.weatherConditionProvider = weatherConditionProvider;
        this.flightFlowConditionProvider = flightFlowConditionProvider;
    }

    @Override
    @Cacheable(cacheNames = "prediction-results", key = "#startDate + '_' + #endDate")
    public CriticalityScore predict(LocalDate startDate, LocalDate endDate) {
        LOGGER.info("Predicting passenger flow criticality for period: {} to {}", startDate, endDate);
        
        try {
            List<Condition> conditions = List.of(
                    weatherConditionProvider.provide(startDate, endDate),
                    flightFlowConditionProvider.provide(startDate, endDate)
            );

            Condition combinedCondition = new SimpleCombinedCondition(conditions);
            CriticalityScore score = combinedCondition.calculateCriticality();
            
            LOGGER.info("Calculated criticality score: {}", score.getScore());
            return score;
            
        } catch (Exception e) {
            LOGGER.error("Failed to predict passenger flow for period: {} to {}", startDate, endDate, e);
            throw new RuntimeException("Passenger flow prediction failed", e);
        }
    }
}
