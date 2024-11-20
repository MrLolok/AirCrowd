package it.lorenzoangelino.aircrowd.prediction.model.conditions.combined.impl;

import it.lorenzoangelino.aircrowd.prediction.calculators.Calculator;
import it.lorenzoangelino.aircrowd.prediction.model.conditions.Condition;
import it.lorenzoangelino.aircrowd.prediction.model.conditions.combined.SimpleCombinedCondition;
import it.lorenzoangelino.aircrowd.prediction.model.criticality.CriticalityScore;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class AirportPassengerFlowCondition extends SimpleCombinedCondition {
    public AirportPassengerFlowCondition(@NotNull List<Condition> conditions) {
        super(conditions);
    }

    @Override
    public CriticalityScore getCriticalityScore() {
        return null;
    }

    @Override
    public @NotNull List<?> getData() {
        return List.of();
    }

    @Override
    public void setData(@NotNull List<?> data) {

    }

    @Override
    protected Calculator<List<?>, Float> getCriticalityCalculator() {
        return null;
    }
}
