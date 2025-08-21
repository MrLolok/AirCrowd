package it.lorenzoangelino.aircrowd.prediction.models.conditions;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import it.lorenzoangelino.aircrowd.prediction.models.conditions.combined.impl.SimpleCombinedCondition;
import it.lorenzoangelino.aircrowd.prediction.models.conditions.combined.impl.WeatherForecastCondition;
import it.lorenzoangelino.aircrowd.prediction.models.conditions.individual.impl.FlightFlowCondition;
import it.lorenzoangelino.aircrowd.prediction.models.conditions.individual.impl.WeatherCondition;
import it.lorenzoangelino.aircrowd.prediction.models.criticality.CriticalityScore;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "type")
@JsonSubTypes({
    @JsonSubTypes.Type(value = WeatherCondition.class, name = "WeatherCondition"),
    @JsonSubTypes.Type(value = FlightFlowCondition.class, name = "FlightFlowCondition"),
    @JsonSubTypes.Type(value = SimpleCombinedCondition.class, name = "SimpleCombinedCondition"),
    @JsonSubTypes.Type(value = WeatherForecastCondition.class, name = "WeatherForecastCondition")
})
public interface Condition {
    CriticalityScore getCriticalityScore();
}
