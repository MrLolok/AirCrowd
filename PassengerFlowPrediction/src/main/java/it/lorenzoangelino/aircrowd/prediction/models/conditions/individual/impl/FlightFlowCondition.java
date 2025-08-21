package it.lorenzoangelino.aircrowd.prediction.models.conditions.individual.impl;

import com.fasterxml.jackson.annotation.JsonTypeName;
import it.lorenzoangelino.aircrowd.common.models.flights.FlightFlowData;
import it.lorenzoangelino.aircrowd.prediction.calculators.Calculator;
import it.lorenzoangelino.aircrowd.prediction.calculators.FlightFlowCriticalityCalculator;
import it.lorenzoangelino.aircrowd.prediction.models.conditions.individual.AbstractIndividualCondition;

@JsonTypeName("FlightFlowCondition")
public class FlightFlowCondition extends AbstractIndividualCondition<FlightFlowData> {
    private static final Calculator<FlightFlowData, Float> FLIGHT_FLOW_CRITICALITY_CALCULATOR =
            new FlightFlowCriticalityCalculator();

    public FlightFlowCondition(FlightFlowData data) {
        super(data);
    }

    @Override
    protected Calculator<FlightFlowData, Float> getCriticalityCalculator() {
        return FLIGHT_FLOW_CRITICALITY_CALCULATOR;
    }
}
