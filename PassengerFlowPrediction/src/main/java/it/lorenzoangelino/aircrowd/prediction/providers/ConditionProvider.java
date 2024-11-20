package it.lorenzoangelino.aircrowd.prediction.providers;

import java.util.Set;

public interface ConditionProvider<T> {
    Set<T> getCache();
}
