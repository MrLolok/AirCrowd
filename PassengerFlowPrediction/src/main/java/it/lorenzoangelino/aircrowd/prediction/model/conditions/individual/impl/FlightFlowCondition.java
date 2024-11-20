package it.lorenzoangelino.aircrowd.prediction.model.conditions.individual.impl;

import it.lorenzoangelino.aircrowd.common.models.flights.FlightFlowData;
import it.lorenzoangelino.aircrowd.prediction.calculators.Calculator;
import it.lorenzoangelino.aircrowd.prediction.calculators.WeatherCriticalityCalculator;
import it.lorenzoangelino.aircrowd.prediction.model.conditions.individual.AbstractIndividualCondition;

public class FlightFlowCondition extends AbstractIndividualCondition<FlightFlowData> {
    private final static Calculator<FlightFlowData, Float> FLIGHT_FLOW_CRITICALITY_CALCULATOR = new WeatherCriticalityCalculator();

    public FlightFlowCondition(FlightFlowData data) {
        super(data);
    }

    @Override
    protected Calculator<FlightFlowData, Float> getCriticalityCalculator() {
        return FLIGHT_FLOW_CRITICALITY_CALCULATOR;
    }
}
