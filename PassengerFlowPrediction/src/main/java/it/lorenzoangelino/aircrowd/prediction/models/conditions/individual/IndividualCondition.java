package it.lorenzoangelino.aircrowd.prediction.models.conditions.individual;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import it.lorenzoangelino.aircrowd.prediction.models.conditions.Condition;
import it.lorenzoangelino.aircrowd.prediction.models.conditions.individual.impl.FlightFlowCondition;
import it.lorenzoangelino.aircrowd.prediction.models.conditions.individual.impl.WeatherCondition;
import org.jetbrains.annotations.NotNull;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "type")
@JsonSubTypes({
    @JsonSubTypes.Type(value = WeatherCondition.class, name = "WeatherCondition"),
    @JsonSubTypes.Type(value = FlightFlowCondition.class, name = "FlightFlowCondition"),
})
public interface IndividualCondition<T> extends Condition {
    @NotNull
    T getData();

    void setData(@NotNull T data);
}
