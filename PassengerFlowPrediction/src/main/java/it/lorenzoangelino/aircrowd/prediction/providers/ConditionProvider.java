package it.lorenzoangelino.aircrowd.prediction.providers;

import it.lorenzoangelino.aircrowd.prediction.models.conditions.Condition;

import java.time.LocalDateTime;
import java.util.Optional;

public interface ConditionProvider {
    Optional<Condition> getCondition(LocalDateTime start, LocalDateTime end);

    default Optional<Condition> getCondition(LocalDateTime datetime) {
        LocalDateTime start = datetime.withMinute(0).withSecond(0);
        LocalDateTime end = start.plusHours(1);
        return getCondition(start, end);
    }
}
