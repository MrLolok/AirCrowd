package it.lorenzoangelino.aircrowd.prediction.model.criticality;

import lombok.Getter;
import org.jetbrains.annotations.Nullable;

import java.util.List;

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
