package it.lorenzoangelino.aircrowd.common.models.predictions;

import it.lorenzoangelino.aircrowd.common.models.IdentifiableModel;
import java.time.LocalDateTime;

public record PassengerFlowPrediction(
        String predictionId,
        LocalDateTime createdAt,
        LocalDateTime predictionTime,
        CriticalityLevel overallCriticality,
        Double criticalityScore,
        Integer predictedPassengerFlow,
        String description
) implements IdentifiableModel<String> {

    @Override
    public String getId() {
        return predictionId;
    }

    public enum CriticalityLevel {
        LOW, MODERATE, MEDIUM, HIGH, SEVERE
    }
}