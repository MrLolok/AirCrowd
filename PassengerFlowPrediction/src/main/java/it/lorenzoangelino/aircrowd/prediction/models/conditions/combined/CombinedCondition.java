package it.lorenzoangelino.aircrowd.prediction.models.conditions.combined;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import it.lorenzoangelino.aircrowd.prediction.models.conditions.Condition;
import it.lorenzoangelino.aircrowd.prediction.models.conditions.combined.impl.SimpleCombinedCondition;
import it.lorenzoangelino.aircrowd.prediction.models.conditions.combined.impl.WeatherForecastCondition;
import org.jetbrains.annotations.NotNull;

import java.util.List;

@JsonTypeInfo(
    use = JsonTypeInfo.Id.NAME,
    property = "type"
)
@JsonSubTypes({
    @JsonSubTypes.Type(value = SimpleCombinedCondition.class, name = "SimpleCombinedCondition"),
    @JsonSubTypes.Type(value = WeatherForecastCondition.class, name = "WeatherForecastCondition")
})
public interface CombinedCondition extends Condition {
    @NotNull List<? extends Condition> getConditions();

    void setConditions(@NotNull List<? extends Condition> individualConditions);
}
