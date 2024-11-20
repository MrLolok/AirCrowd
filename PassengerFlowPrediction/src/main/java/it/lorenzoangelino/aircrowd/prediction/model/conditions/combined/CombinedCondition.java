package it.lorenzoangelino.aircrowd.prediction.model.conditions.combined;

import it.lorenzoangelino.aircrowd.prediction.model.conditions.Condition;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public interface CombinedCondition extends Condition {
    @NotNull List<Condition> getConditions();

    void setConditions(@NotNull List<Condition> individualConditions);
}
