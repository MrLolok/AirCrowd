package it.lorenzoangelino.aircrowd.prediction.calculators;

import static it.lorenzoangelino.aircrowd.prediction.utils.DataNormalizer.normalize;

import it.lorenzoangelino.aircrowd.prediction.configs.ConfigCriticalitySettings;
import java.util.List;
import org.jetbrains.annotations.NotNull;

public abstract class AbstractCriticalityCalculator<T> implements Calculator<T, Float> {
    @Override
    public Float calculate(T input) {
        double criticality = 0;

        ConfigCriticalitySettings settings = getCriticalitySettings();
        List<ConfigCriticalitySettings.Parameter> parameters = settings.parameters();

        Double[] values = getNumericValues(input);
        for (int i = 0; i < values.length && i < parameters.size(); i++) {
            ConfigCriticalitySettings.Parameter parameter = parameters.get(i);
            double normalized = normalize(
                    Math.abs(values[i] - parameter.deviation()),
                    parameter.minValue(),
                    parameter.maxValue()); // Deviazione dalla temperatura ottimale
            criticality += (parameter.invertedImpact() ? 1 - normalized : normalized) * parameter.weight();
        }
        return (float) Math.max(settings.minCriticalityValue(), Math.min(settings.maxCriticalityValue(), criticality));
    }

    protected abstract @NotNull Double[] getNumericValues(T input);

    protected abstract @NotNull ConfigCriticalitySettings getCriticalitySettings();
}
