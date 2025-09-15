package it.lorenzoangelino.aircrowd.prediction.services;

import it.lorenzoangelino.aircrowd.common.models.predictions.PassengerFlowPrediction;
import it.lorenzoangelino.aircrowd.prediction.providers.ConditionProvider;
import org.springframework.cache.annotation.Cacheable;

import java.time.LocalDateTime;

public interface PassengerFlowPredictionService extends ConditionProvider {
    @Cacheable(cacheNames = "prediction-results", key = "#start + '_' + #end")
    PassengerFlowPrediction predictPassengerFlow(LocalDateTime start, LocalDateTime end);
}
