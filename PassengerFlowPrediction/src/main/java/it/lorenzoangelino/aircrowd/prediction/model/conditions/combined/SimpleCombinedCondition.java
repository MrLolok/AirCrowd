package it.lorenzoangelino.aircrowd.prediction.model.conditions.combined;

import it.lorenzoangelino.aircrowd.prediction.model.conditions.AbstractCondition;
import it.lorenzoangelino.aircrowd.prediction.model.conditions.Condition;
import it.lorenzoangelino.aircrowd.prediction.model.criticality.CombinedCriticalityScore;
import it.lorenzoangelino.aircrowd.prediction.model.criticality.SimpleCombinedCriticalityScore;
import it.lorenzoangelino.aircrowd.prediction.model.criticality.CriticalityScore;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;

import java.util.List;

@Getter
public class SimpleCombinedCondition extends AbstractCondition implements CombinedCondition {
    private @NotNull List<Condition> conditions;

    public SimpleCombinedCondition(@NotNull List<Condition> conditions) {
        setConditions(conditions);
    }

    @Override
    public void setConditions(@NotNull List<Condition> conditions) {
        this.conditions = conditions;
        updateCriticalityScore();
    }

    protected CombinedCriticalityScore getCombinedCriticalityScore() {
        List<CriticalityScore> scores = conditions.stream()
                .map(Condition::getCriticalityScore)
                .toList();
        return new SimpleCombinedCriticalityScore(scores);
    }

    private void updateCriticalityScore() {
        CombinedCriticalityScore score = getCombinedCriticalityScore();
        super.setCriticalityScore(score);
    }
}
