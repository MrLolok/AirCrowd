package it.lorenzoangelino.aircrowd.prediction.services;

import it.lorenzoangelino.aircrowd.prediction.configs.ConfigCriticalitySettings;
import it.lorenzoangelino.aircrowd.prediction.configs.WeatherCriticalityConfig;
import it.lorenzoangelino.aircrowd.prediction.configs.FlightFlowCriticalityConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class CriticalityConfigurationService {

    private final WeatherCriticalityConfig weatherConfig;
    private final FlightFlowCriticalityConfig flightFlowConfig;

    public ConfigCriticalitySettings getWeatherCriticalitySettings() {
        return new ConfigCriticalitySettings(
                weatherConfig.parameters(),
                weatherConfig.minCriticalityValue(),
                weatherConfig.maxCriticalityValue()
        );
    }

    public ConfigCriticalitySettings getFlightFlowCriticalitySettings() {
        return new ConfigCriticalitySettings(
                flightFlowConfig.parameters(),
                flightFlowConfig.minCriticalityValue(),
                flightFlowConfig.maxCriticalityValue()
        );
    }
}