package it.lorenzoangelino.aircrowd.prediction.model.conditions;

import it.lorenzoangelino.aircrowd.prediction.model.criticality.CriticalityScore;

public interface Condition {
    CriticalityScore getCriticalityScore();
}
