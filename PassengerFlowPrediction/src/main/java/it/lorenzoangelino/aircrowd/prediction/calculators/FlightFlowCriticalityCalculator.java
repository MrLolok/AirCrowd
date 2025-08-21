package it.lorenzoangelino.aircrowd.prediction.calculators;

import it.lorenzoangelino.aircrowd.common.configs.ConfigProvider;
import it.lorenzoangelino.aircrowd.common.models.flights.FlightData;
import it.lorenzoangelino.aircrowd.common.models.flights.FlightFlowData;
import it.lorenzoangelino.aircrowd.prediction.configs.ConfigCriticalitySettings;
import org.jetbrains.annotations.NotNull;

public class FlightFlowCriticalityCalculator extends AbstractCriticalityCalculator<FlightFlowData> {
    private static final ConfigCriticalitySettings FLIGHT_FLOW_CRITICALITY_CONFIG =
            ConfigProvider.getInstance().loadConfig("flight-flow-criticality", ConfigCriticalitySettings.class);

    @Override
    protected @NotNull Double[] getNumericValues(FlightFlowData input) {
        return new Double[] {
            (double) input.flights().stream().mapToInt(FlightData::seats).sum()
        };
    }

    @Override
    protected @NotNull ConfigCriticalitySettings getCriticalitySettings() {
        return FLIGHT_FLOW_CRITICALITY_CONFIG;
    }
}
