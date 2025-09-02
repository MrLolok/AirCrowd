package it.lorenzoangelino.aircrowd.prediction.models.criticality;

import com.fasterxml.jackson.annotation.JsonTypeName;
import it.lorenzoangelino.aircrowd.common.models.predictions.PassengerFlowPrediction;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;

@JsonTypeName("SimpleCriticalityScore")
@NoArgsConstructor
@Getter
@Setter
public class SimpleCriticalityScore implements CriticalityScore {
    // Maximum criticality
    private static final PassengerFlowPrediction.CriticalityLevel DEFAULT_CRITICALITY_LEVEL = PassengerFlowPrediction.CriticalityLevel.SEVERE;
    private float value = 0f;

    public SimpleCriticalityScore(float score) {
        this.value = score;
    }

    public @NotNull PassengerFlowPrediction.CriticalityLevel getLevel() {
        float score = Math.max(0, Math.min(1, this.value));
        PassengerFlowPrediction.CriticalityLevel[] levels = PassengerFlowPrediction.CriticalityLevel.values();
        float offset = 1F / levels.length;
        for (int i = 0; i < levels.length; i++) {
            if (score < offset * (i + 1)) return levels[i];
        }
        return DEFAULT_CRITICALITY_LEVEL;
    }
}
