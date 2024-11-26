package it.lorenzoangelino.aircrowd.prediction.models.conditions.individual;

import it.lorenzoangelino.aircrowd.prediction.calculators.Calculator;
import it.lorenzoangelino.aircrowd.prediction.models.conditions.AbstractCondition;
import it.lorenzoangelino.aircrowd.prediction.models.criticality.CriticalityScore;
import it.lorenzoangelino.aircrowd.prediction.models.criticality.SimpleCriticalityScore;
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
