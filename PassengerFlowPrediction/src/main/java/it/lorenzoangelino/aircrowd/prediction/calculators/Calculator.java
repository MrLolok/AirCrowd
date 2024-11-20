package it.lorenzoangelino.aircrowd.prediction.calculators;

public interface Calculator<I, O> {
    O calculate(I input);
}
