package it.lorenzoangelino.aircrowd.prediction.calculators;

import it.lorenzoangelino.aircrowd.common.models.flights.FlightData;
import it.lorenzoangelino.aircrowd.common.models.flights.FlightFlowData;
import it.lorenzoangelino.aircrowd.prediction.configs.ConfigCriticalitySettings;
import it.lorenzoangelino.aircrowd.prediction.services.CriticalityConfigurationService;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class FlightFlowCriticalityCalculator extends AbstractCriticalityCalculator<FlightFlowData> {

    private final CriticalityConfigurationService configService;

    @Override
    protected @NotNull Double[] getNumericValues(FlightFlowData input) {
        return new Double[] {
            (double) input.flights().stream().mapToInt(FlightData::seats).sum()
        };
    }

    @Override
    protected @NotNull ConfigCriticalitySettings getCriticalitySettings() {
        return configService.getFlightFlowCriticalitySettings();
    }
}
