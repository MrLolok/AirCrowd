package it.lorenzoangelino.aircrowd.prediction.model.conditions.individual;

import it.lorenzoangelino.aircrowd.prediction.model.conditions.Condition;
import org.jetbrains.annotations.NotNull;

public interface IndividualCondition<T> extends Condition {
    @NotNull T getData();

    void setData(@NotNull T data);
}
