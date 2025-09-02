package it.lorenzoangelino.aircrowd.airportmanagement.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import it.lorenzoangelino.aircrowd.common.models.predictions.PassengerFlowPrediction;
import java.time.LocalDateTime;

public record PredictionResponse(
        String predictionId,
        @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss") LocalDateTime timestamp,
        @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss") LocalDateTime predictionTime,
        PassengerFlowPrediction.CriticalityLevel overallCriticality,
        Double criticalityScore,
        Integer predictedPassengerFlow,
        String description) {
}
