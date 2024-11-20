package it.lorenzoangelino.aircrowd.prediction.model.conditions;

import it.lorenzoangelino.aircrowd.prediction.model.criticality.CriticalityScore;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;

@Getter
public abstract class AbstractCondition implements Condition {
    @Setter(value = AccessLevel.PROTECTED)
    private @NotNull CriticalityScore criticalityScore;
}
