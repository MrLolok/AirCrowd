package it.lorenzoangelino.aircrowd.prediction.models.criticality;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import java.util.List;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "type")
@JsonSubTypes({@JsonSubTypes.Type(value = SimpleCombinedCriticalityScore.class, name = "SimpleCombinedCriticalityScore")
})
public interface CombinedCriticalityScore extends CriticalityScore {
    List<CriticalityScore> getCriticalityScores();

    void setCriticalityScores(List<CriticalityScore> scores);

    default float getCombinedCriticalityValue() {
        return getCriticalityScores().isEmpty()
                ? 0f
                : (float) getCriticalityScores().stream()
                                .mapToDouble(CriticalityScore::getValue)
                                .sum()
                        / getCriticalityScores().size();
    }
}
