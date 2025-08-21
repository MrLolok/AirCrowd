package it.lorenzoangelino.aircrowd.prediction.models.criticality;

import com.fasterxml.jackson.annotation.JsonTypeName;
import it.lorenzoangelino.aircrowd.prediction.models.criticality.enums.CriticalityLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;

@JsonTypeName("SimpleCriticalityScore")
@NoArgsConstructor
@Getter
@Setter
public class SimpleCriticalityScore implements CriticalityScore {
    // Maximum criticality
    private static final CriticalityLevel DEFAULT_CRITICALITY_LEVEL = CriticalityLevel.SEVERE;
    private float value = 0f;

    public SimpleCriticalityScore(float score) {
        this.value = score;
    }

    public @NotNull CriticalityLevel getLevel() {
        float score = Math.max(0, Math.min(1, this.value));
        int levels = CriticalityLevel.values().length;
        float offset = 1F / levels;
        for (int i = 0; i < levels; i++) if (score < offset * (i + 1)) return CriticalityLevel.values()[i];
        return DEFAULT_CRITICALITY_LEVEL;
    }
}
