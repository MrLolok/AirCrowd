package it.lorenzoangelino.aircrowd.prediction.models.conditions.combined.impl;

import com.fasterxml.jackson.annotation.JsonTypeName;
import it.lorenzoangelino.aircrowd.prediction.models.conditions.AbstractCondition;
import it.lorenzoangelino.aircrowd.prediction.models.conditions.Condition;
import it.lorenzoangelino.aircrowd.prediction.models.conditions.combined.CombinedCondition;
import it.lorenzoangelino.aircrowd.prediction.models.criticality.CombinedCriticalityScore;
import it.lorenzoangelino.aircrowd.prediction.models.criticality.CriticalityScore;
import it.lorenzoangelino.aircrowd.prediction.models.criticality.SimpleCombinedCriticalityScore;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.jetbrains.annotations.NotNull;

@JsonTypeName("SimpleCombinedCondition")
@NoArgsConstructor
@Getter
public class SimpleCombinedCondition extends AbstractCondition implements CombinedCondition {
    private @NotNull List<? extends Condition> conditions = new ArrayList<>();

    public SimpleCombinedCondition(@NotNull List<? extends Condition> conditions) {
        setConditions(conditions);
    }

    @Override
    public void setConditions(@NotNull List<? extends Condition> conditions) {
        this.conditions = conditions;
        updateCriticalityScore();
    }

    protected CombinedCriticalityScore getCombinedCriticalityScore() {
        List<CriticalityScore> scores =
                conditions.stream().map(Condition::getCriticalityScore).toList();
        return new SimpleCombinedCriticalityScore(scores);
    }

    private void updateCriticalityScore() {
        CombinedCriticalityScore score = getCombinedCriticalityScore();
        super.setCriticalityScore(score);
    }
}
