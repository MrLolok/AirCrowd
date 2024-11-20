package it.lorenzoangelino.aircrowd.prediction.model.conditions.individual;

import it.lorenzoangelino.aircrowd.prediction.calculators.Calculator;
import it.lorenzoangelino.aircrowd.prediction.model.conditions.AbstractCondition;
import it.lorenzoangelino.aircrowd.prediction.model.criticality.CriticalityScore;
import it.lorenzoangelino.aircrowd.prediction.model.criticality.SimpleCriticalityScore;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;

@Getter
public abstract class AbstractIndividualCondition<T> extends AbstractCondition implements IndividualCondition<T> {
    private @NotNull T data;

    protected AbstractIndividualCondition(T data) {
        setData(data);
    }

    @Override
    public void setData(@NotNull T data) {
        this.data = data;
        Calculator<T, Float> calculator = getCriticalityCalculator();
        float criticality = calculator.calculate(data);
        CriticalityScore score = new SimpleCriticalityScore(criticality);
        setCriticalityScore(score);
    }

    protected abstract Calculator<T, Float> getCriticalityCalculator();
}
