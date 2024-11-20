package it.lorenzoangelino.aircrowd.prediction.model.criticality;

import java.util.List;

public interface CombinedCriticalityScore extends CriticalityScore {
    List<CriticalityScore> getCriticalityScores();

    void setCriticalityScores(List<CriticalityScore> scores);

    default float getCombinedCriticalityValue() {
        return (float) getCriticalityScores().stream().mapToDouble(CriticalityScore::getValue).sum();
    }
}
