package it.lorenzoangelino.aircrowd.prediction.providers;

import it.lorenzoangelino.aircrowd.prediction.models.conditions.Condition;
import java.time.LocalDateTime;
import java.util.Optional;

public interface ConditionProvider {
    Optional<Condition> getCondition(LocalDateTime start, LocalDateTime end);
}
