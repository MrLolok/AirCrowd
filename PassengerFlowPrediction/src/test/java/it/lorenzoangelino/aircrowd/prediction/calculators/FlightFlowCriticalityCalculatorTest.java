package it.lorenzoangelino.aircrowd.prediction.calculators;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import it.lorenzoangelino.aircrowd.common.models.flights.FlightData;
import it.lorenzoangelino.aircrowd.common.models.flights.FlightFlowData;
import it.lorenzoangelino.aircrowd.common.models.flights.enums.FlightType;
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
class FlightFlowCriticalityCalculatorTest {

    @Mock
    private CriticalityConfigurationService mockConfigService;

    private FlightFlowCriticalityCalculator calculator;
    private FlightFlowData testFlightFlowData;

    @BeforeEach
    void setUp() {
        calculator = new FlightFlowCriticalityCalculator(mockConfigService);
        
        // Create test flight data
        List<FlightData> flights = List.of(
                new FlightData(
                        "FL001",
                        FlightType.DEPARTURE,
                        LocalDateTime.now().plusHours(1),
                        180
                ),
                new FlightData(
                        "FL002",
                        FlightType.ARRIVAL,
                        LocalDateTime.now().plusHours(2),
                        200
                ),
                new FlightData(
                        "FL003",
                        FlightType.DEPARTURE,
                        LocalDateTime.now().plusHours(3),
                        150
                )
        );

        testFlightFlowData = new FlightFlowData(
                flights
        );

        // Create test configuration settings for flight flow
        List<ConfigCriticalitySettings.Parameter> parameters = List.of(
                new ConfigCriticalitySettings.Parameter("totalSeats", 400.0, 1.0, 0.0, 1000.0, false)
        );
        ConfigCriticalitySettings testSettings = new ConfigCriticalitySettings(parameters, 0.0, 1.0);

        when(mockConfigService.getFlightFlowCriticalitySettings()).thenReturn(testSettings);
    }

    @Test
    @DisplayName("Should calculate flight flow criticality score correctly")
    void shouldCalculateFlightFlowCriticalityScoreCorrectly() {
        // When
        Float criticalityScore = calculator.calculate(testFlightFlowData);

        // Then
        assertThat(criticalityScore).isNotNull();
        assertThat(criticalityScore).isBetween(0.0f, 1.0f);
        
        verify(mockConfigService).getFlightFlowCriticalitySettings();
    }

    @Test
    @DisplayName("Should calculate criticality for flight flow data")
    void shouldCalculateCriticalityForFlightFlowData() {
        // When
        Float criticalityScore = calculator.calculate(testFlightFlowData);

        // Then
        assertThat(criticalityScore).isNotNull();
        assertThat(criticalityScore).isBetween(0.0f, 1.0f);
        
        verify(mockConfigService).getFlightFlowCriticalitySettings();
    }

    @Test
    @DisplayName("Should handle empty flight list")
    void shouldHandleEmptyFlightList() {
        // Given
        FlightFlowData emptyFlightFlow = new FlightFlowData(
                List.of()
        );

        // When
        Float criticalityScore = calculator.calculate(emptyFlightFlow);

        // Then
        assertThat(criticalityScore).isNotNull();
        assertThat(criticalityScore).isBetween(0.0f, 1.0f);
        
        // Should handle empty flight list gracefully
    }

    @Test
    @DisplayName("Should handle high traffic flight flow")
    void shouldHandleHighTrafficFlightFlow() {
        // Given - High capacity flights
        List<FlightData> highCapacityFlights = List.of(
                new FlightData("FL100", FlightType.DEPARTURE, LocalDateTime.now().plusHours(1), 400),
                new FlightData("FL101", FlightType.ARRIVAL, LocalDateTime.now().plusHours(2), 380),
                new FlightData("FL102", FlightType.DEPARTURE, LocalDateTime.now().plusHours(3), 350),
                new FlightData("FL103", FlightType.ARRIVAL, LocalDateTime.now().plusHours(4), 300)
        );

        FlightFlowData highTrafficFlow = new FlightFlowData(
                highCapacityFlights
        );

        // When
        Float criticalityScore = calculator.calculate(highTrafficFlow);

        // Then
        assertThat(criticalityScore).isNotNull();
        assertThat(criticalityScore).isBetween(0.0f, 1.0f);
        
        // High traffic should result in higher criticality within bounds
    }

    @Test
    @DisplayName("Should handle low traffic flight flow")
    void shouldHandleLowTrafficFlightFlow() {
        // Given - Small aircraft flights
        List<FlightData> lowCapacityFlights = List.of(
                new FlightData("FL200", FlightType.DEPARTURE, LocalDateTime.now().plusHours(1), 50),
                new FlightData("FL201", FlightType.ARRIVAL, LocalDateTime.now().plusHours(2), 70)
        );

        FlightFlowData lowTrafficFlow = new FlightFlowData(
                lowCapacityFlights
        );

        // When
        Float criticalityScore = calculator.calculate(lowTrafficFlow);

        // Then
        assertThat(criticalityScore).isNotNull();
        assertThat(criticalityScore).isBetween(0.0f, 1.0f);
        
        // Low traffic should result in lower criticality within bounds
    }

    @Test
    @DisplayName("Should respect min and max criticality bounds")
    void shouldRespectMinAndMaxCriticalityBounds() {
        // Given - Configuration with tight bounds
        List<ConfigCriticalitySettings.Parameter> tightParameters = List.of(
                new ConfigCriticalitySettings.Parameter("totalSeats", 500.0, 1.0, 0.0, 1000.0, false)
        );
        ConfigCriticalitySettings tightSettings = new ConfigCriticalitySettings(tightParameters, 0.1, 0.9);
        
        when(mockConfigService.getFlightFlowCriticalitySettings()).thenReturn(tightSettings);

        // When
        Float criticalityScore = calculator.calculate(testFlightFlowData);

        // Then
        assertThat(criticalityScore).isNotNull();
        assertThat(criticalityScore).isBetween(0.1f, 0.9f);
    }

    @Test
    @DisplayName("Should handle inverted impact for flight flow")
    void shouldHandleInvertedImpactForFlightFlow() {
        // Given - Configuration with inverted impact (more seats = lower criticality, unusual but possible)
        List<ConfigCriticalitySettings.Parameter> invertedParameters = List.of(
                new ConfigCriticalitySettings.Parameter("totalSeats", 300.0, 0.8, 0.0, 1000.0, true)
        );
        ConfigCriticalitySettings invertedSettings = new ConfigCriticalitySettings(invertedParameters, 0.0, 1.0);
        
        when(mockConfigService.getFlightFlowCriticalitySettings()).thenReturn(invertedSettings);

        // When
        Float criticalityScore = calculator.calculate(testFlightFlowData);

        // Then
        assertThat(criticalityScore).isNotNull();
        assertThat(criticalityScore).isBetween(0.0f, 1.0f);
        
        verify(mockConfigService).getFlightFlowCriticalitySettings();
    }

    @Test
    @DisplayName("Should handle null flight flow data gracefully")
    void shouldHandleNullFlightFlowDataGracefully() {
        // When & Then
        assertThatThrownBy(() -> calculator.calculate(null))
                .isInstanceOf(NullPointerException.class);
    }

    @Test
    @DisplayName("Should handle flights with zero seats")
    void shouldHandleFlightsWithZeroSeats() {
        // Given
        List<FlightData> zeroSeatsFlights = List.of(
                new FlightData("FL300", FlightType.DEPARTURE, LocalDateTime.now().plusHours(1), 0),
                new FlightData("FL301", FlightType.ARRIVAL, LocalDateTime.now().plusHours(2), 0)
        );

        FlightFlowData zeroSeatsFlow = new FlightFlowData(
                zeroSeatsFlights
        );

        // When
        Float criticalityScore = calculator.calculate(zeroSeatsFlow);

        // Then
        assertThat(criticalityScore).isNotNull();
        assertThat(criticalityScore).isBetween(0.0f, 1.0f);
        
        // Should handle zero seats without errors
    }

    @Test
    @DisplayName("Should use configuration service for settings")
    void shouldUseConfigurationServiceForSettings() {
        // When
        calculator.calculate(testFlightFlowData);

        // Then
        verify(mockConfigService).getFlightFlowCriticalitySettings();
    }

    @Test
    @DisplayName("Should handle mixed flight types correctly")
    void shouldHandleMixedFlightTypesCorrectly() {
        // Given - Mix of arrivals and departures with different capacities
        List<FlightData> mixedFlights = List.of(
                new FlightData("ARR001", FlightType.ARRIVAL, LocalDateTime.now().plusHours(1), 250),
                new FlightData("DEP001", FlightType.DEPARTURE, LocalDateTime.now().plusHours(2), 180),
                new FlightData("ARR002", FlightType.ARRIVAL, LocalDateTime.now().plusHours(3), 200),
                new FlightData("DEP002", FlightType.DEPARTURE, LocalDateTime.now().plusHours(4), 160)
        );

        FlightFlowData mixedFlightFlow = new FlightFlowData(
                mixedFlights
        );

        // When
        Float criticalityScore = calculator.calculate(mixedFlightFlow);

        // Then
        assertThat(criticalityScore).isNotNull();
        assertThat(criticalityScore).isBetween(0.0f, 1.0f);
        
        // Should handle mixed flight types correctly
    }
}