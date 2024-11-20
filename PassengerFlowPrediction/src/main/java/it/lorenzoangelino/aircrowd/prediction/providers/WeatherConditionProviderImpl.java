package it.lorenzoangelino.aircrowd.prediction.providers;

import it.lorenzoangelino.aircrowd.prediction.model.conditions.Condition;
import it.lorenzoangelino.aircrowd.prediction.model.conditions.combined.CombinedCondition;
import lombok.Getter;

import java.util.HashSet;
import java.util.Set;

public class WeatherConditionProviderImpl implements ConditionProvider<Condition> {
    @Getter
    private final Set<CombinedCondition> cache;

    public WeatherConditionProviderImpl() {
        this.cache = new HashSet<>();
    }
}
