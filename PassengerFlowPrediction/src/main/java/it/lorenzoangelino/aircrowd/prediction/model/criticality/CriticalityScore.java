package it.lorenzoangelino.aircrowd.prediction.model.criticality;

import it.lorenzoangelino.aircrowd.prediction.model.criticality.enums.CriticalityLevel;
import org.jetbrains.annotations.NotNull;

public interface CriticalityScore {
    float getValue();

    void setValue(float value);

    @NotNull CriticalityLevel getLevel();
}
