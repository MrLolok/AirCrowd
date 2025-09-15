package it.lorenzoangelino.aircrowd.prediction.calculators;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import it.lorenzoangelino.aircrowd.common.models.locations.GeographicalLocation;
import it.lorenzoangelino.aircrowd.common.models.weather.WeatherData;
import it.lorenzoangelino.aircrowd.prediction.configs.ConfigCriticalitySettings;
import it.lorenzoangelino.aircrowd.prediction.services.CriticalityConfigurationService;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

@ExtendWith(MockitoExtension.class)
@SpringBootTest
@TestPropertySource(properties = {"spring.cloud.config.enabled=false"})
class WeatherCriticalityCalculatorTest {

    @Mock
    private CriticalityConfigurationService mockConfigService;

    private WeatherCriticalityCalculator calculator;
    private WeatherData testWeatherData;
    private ConfigCriticalitySettings testSettings;

    @BeforeEach
    void setUp() {
        calculator = new WeatherCriticalityCalculator(mockConfigService);
        
        // Create test weather data
        GeographicalLocation location = new GeographicalLocation("Rome", 41.9028, 12.4964);
        testWeatherData = new WeatherData(
                location,
                LocalDateTime.now(),
                LocalDateTime.now().plusHours(1),
                25.0,  // temperature
                65,    // humidity
                0.5,   // rain
                0.0,   // showers
                0.0,   // snowfall
                40,    // cloudCover
                10000, // visibility
                15.0   // windSpeed
        );

        // Create test configuration settings
        List<ConfigCriticalitySettings.Parameter> parameters = List.of(
                new ConfigCriticalitySettings.Parameter("temperature", 20.0, 0.3, 0.0, 50.0, false),
                new ConfigCriticalitySettings.Parameter("humidity", 50.0, 0.2, 0.0, 100.0, false),
                new ConfigCriticalitySettings.Parameter("rain", 0.0, 0.25, 0.0, 10.0, false),
                new ConfigCriticalitySettings.Parameter("windSpeed", 10.0, 0.15, 0.0, 100.0, false)
        );
        testSettings = new ConfigCriticalitySettings(parameters, 0.0, 1.0);

        when(mockConfigService.getWeatherCriticalitySettings()).thenReturn(testSettings);
    }

    @Test
    @DisplayName("Should calculate weather criticality score correctly")
    void shouldCalculateWeatherCriticalityScoreCorrectly() {
        // When
        Float criticalityScore = calculator.calculate(testWeatherData);

        // Then
        assertThat(criticalityScore).isNotNull();
        assertThat(criticalityScore).isBetween(0.0f, 1.0f);
        
        verify(mockConfigService).getWeatherCriticalitySettings();
    }

    @Test
    @DisplayName("Should calculate criticality score within valid range")
    void shouldCalculateCriticalityScoreWithinValidRange() {
        // When
        Float criticalityScore = calculator.calculate(testWeatherData);

        // Then
        assertThat(criticalityScore).isNotNull();
        assertThat(criticalityScore).isBetween(0.0f, 1.0f);
        
        verify(mockConfigService).getWeatherCriticalitySettings();
    }

    @Test
    @DisplayName("Should handle extreme weather conditions")
    void shouldHandleExtremeWeatherConditions() {
        // Given - Extreme weather data
        GeographicalLocation location = new GeographicalLocation("Storm", 45.0, 9.0);
        WeatherData extremeWeather = new WeatherData(
                location,
                LocalDateTime.now(),
                LocalDateTime.now().plusHours(1),
                -10.0, // Very cold temperature
                95,    // High humidity
                15.0,  // Heavy rain
                5.0,   // Showers
                10.0,  // Snowfall
                90,    // High cloud cover
                1000,  // Low visibility
                50.0   // High wind speed
        );

        // When
        Float criticalityScore = calculator.calculate(extremeWeather);

        // Then
        assertThat(criticalityScore).isNotNull();
        assertThat(criticalityScore).isBetween(0.0f, 1.0f);
        // Extreme conditions should result in higher criticality
        assertThat(criticalityScore).isGreaterThan(0.5f);
    }

    @Test
    @DisplayName("Should handle perfect weather conditions")
    void shouldHandlePerfectWeatherConditions() {
        // Given - Perfect weather data matching deviation values
        GeographicalLocation location = new GeographicalLocation("Perfect", 45.0, 9.0);
        WeatherData perfectWeather = new WeatherData(
                location,
                LocalDateTime.now(),
                LocalDateTime.now().plusHours(1),
                20.0,  // Perfect temperature (matches deviation)
                50,    // Perfect humidity (matches deviation)
                0.0,   // No rain (matches deviation)
                0.0,   // No showers
                0.0,   // No snowfall
                30,    // Moderate cloud cover
                10000, // Perfect visibility
                10.0   // Perfect wind speed (matches deviation)
        );

        // When
        Float criticalityScore = calculator.calculate(perfectWeather);

        // Then
        assertThat(criticalityScore).isNotNull();
        assertThat(criticalityScore).isBetween(0.0f, 1.0f);
        // Perfect conditions should result in lower criticality
        assertThat(criticalityScore).isLessThan(0.3f);
    }

    @Test
    @DisplayName("Should respect min and max criticality bounds")
    void shouldRespectMinAndMaxCriticalityBounds() {
        // Given - Configuration with tight bounds
        List<ConfigCriticalitySettings.Parameter> tightParameters = List.of(
                new ConfigCriticalitySettings.Parameter("temperature", 20.0, 1.0, 0.0, 50.0, false)
        );
        ConfigCriticalitySettings tightSettings = new ConfigCriticalitySettings(tightParameters, 0.2, 0.8);
        
        when(mockConfigService.getWeatherCriticalitySettings()).thenReturn(tightSettings);

        // When
        Float criticalityScore = calculator.calculate(testWeatherData);

        // Then
        assertThat(criticalityScore).isNotNull();
        assertThat(criticalityScore).isBetween(0.2f, 0.8f);
    }

    @Test
    @DisplayName("Should handle inverted impact parameters")
    void shouldHandleInvertedImpactParameters() {
        // Given - Configuration with inverted impact (higher visibility = lower criticality)
        List<ConfigCriticalitySettings.Parameter> invertedParameters = List.of(
                new ConfigCriticalitySettings.Parameter("visibility", 5000.0, 0.5, 0.0, 10000.0, true)
        );
        ConfigCriticalitySettings invertedSettings = new ConfigCriticalitySettings(invertedParameters, 0.0, 1.0);
        
        when(mockConfigService.getWeatherCriticalitySettings()).thenReturn(invertedSettings);

        // When
        Float criticalityScore = calculator.calculate(testWeatherData);

        // Then
        assertThat(criticalityScore).isNotNull();
        assertThat(criticalityScore).isBetween(0.0f, 1.0f);
        
        verify(mockConfigService).getWeatherCriticalitySettings();
    }

    @Test
    @DisplayName("Should handle null weather data gracefully")
    void shouldHandleNullWeatherDataGracefully() {
        // When & Then
        assertThatThrownBy(() -> calculator.calculate(null))
                .isInstanceOf(NullPointerException.class);
    }

    @Test
    @DisplayName("Should handle empty parameters configuration")
    void shouldHandleEmptyParametersConfiguration() {
        // Given - Empty parameters
        ConfigCriticalitySettings emptySettings = new ConfigCriticalitySettings(List.of(), 0.0, 1.0);
        when(mockConfigService.getWeatherCriticalitySettings()).thenReturn(emptySettings);

        // When
        Float criticalityScore = calculator.calculate(testWeatherData);

        // Then
        assertThat(criticalityScore).isNotNull();
        assertThat(criticalityScore).isEqualTo(0.0f); // Should be minimum value when no parameters
    }

    @Test
    @DisplayName("Should use configuration service for settings")
    void shouldUseConfigurationServiceForSettings() {
        // When
        calculator.calculate(testWeatherData);

        // Then
        verify(mockConfigService).getWeatherCriticalitySettings();
    }
}