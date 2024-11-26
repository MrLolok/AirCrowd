package it.lorenzoangelino.aircrowd.prediction.models.criticality;

import com.fasterxml.jackson.annotation.JsonTypeName;
import lombok.Getter;
import org.jetbrains.annotations.Nullable;

import java.util.List;

@JsonTypeName("SimpleCombinedCriticalityScore")
@Getter
public class SimpleCombinedCriticalityScore extends SimpleCriticalityScore implements CombinedCriticalityScore {
    private @Nullable List<CriticalityScore> criticalityScores;

    public SimpleCombinedCriticalityScore(List<CriticalityScore> scores) {
        setCriticalityScores(scores);
    }

    @Override
    public void setCriticalityScores(List<CriticalityScore> scores) {
        this.criticalityScores = scores;
        float score = getCombinedCriticalityValue();
        setValue(score);
    }
}
