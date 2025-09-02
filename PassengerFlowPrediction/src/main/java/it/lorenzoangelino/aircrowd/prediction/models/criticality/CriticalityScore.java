package it.lorenzoangelino.aircrowd.prediction.models.criticality;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import it.lorenzoangelino.aircrowd.common.models.predictions.PassengerFlowPrediction;
import org.jetbrains.annotations.NotNull;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "type")
@JsonSubTypes({
    @JsonSubTypes.Type(value = SimpleCriticalityScore.class, name = "SimpleCriticalityScore"),
    @JsonSubTypes.Type(value = SimpleCombinedCriticalityScore.class, name = "SimpleCombinedCriticalityScore")
})
public interface CriticalityScore {
    float getValue();

    void setValue(float value);

    @NotNull
    PassengerFlowPrediction.CriticalityLevel getLevel();
}
