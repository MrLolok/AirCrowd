package it.lorenzoangelino.aircrowd.prediction.calculators;

import static org.assertj.core.api.Assertions.*;

import it.lorenzoangelino.aircrowd.prediction.configs.ConfigCriticalitySettings;
import java.util.List;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

@ExtendWith(MockitoExtension.class)
@SpringBootTest
@TestPropertySource(properties = {"spring.cloud.config.enabled=false"})
class AbstractCriticalityCalculatorTest {

    private TestCriticalityCalculator calculator;

    @BeforeEach
    void setUp() {
        calculator = new TestCriticalityCalculator();
    }

    @Test
    @DisplayName("Should calculate criticality with single parameter")
    void shouldCalculateCriticalityWithSingleParameter() {
        // Given
        TestInput input = new TestInput(25.0); // Temperature 5 degrees above optimal (20)
        
        // When
        Float result = calculator.calculate(input);

        // Then
        assertThat(result).isNotNull();
        assertThat(result).isBetween(0.0f, 1.0f);
        // Expected: deviation = |25-20| = 5, normalized = 5/30 = 0.167, result = 0.167 * 0.5 = 0.0833
        assertThat(result).isCloseTo(0.083f, within(0.01f));
    }

    @Test
    @DisplayName("Should calculate criticality with multiple parameters")
    void shouldCalculateCriticalityWithMultipleParameters() {
        // Given
        TestInput input = new TestInput(25.0, 80.0); // Temperature +5 from optimal, humidity +30 from optimal
        
        // When
        Float result = calculator.calculate(input);

        // Then
        assertThat(result).isNotNull();
        assertThat(result).isBetween(0.0f, 1.0f);
        // Should consider both temperature and humidity parameters
        assertThat(result).isGreaterThan(0.1f);
    }

    @Test
    @DisplayName("Should handle inverted impact parameter")
    void shouldHandleInvertedImpactParameter() {
        // Given - Using visibility parameter which has inverted impact
        TestInput input = new TestInput(25.0, 80.0, 8000.0); // High visibility should reduce criticality
        
        // When
        Float result = calculator.calculate(input);

        // Then
        assertThat(result).isNotNull();
        assertThat(result).isBetween(0.0f, 1.0f);
    }

    @Test
    @DisplayName("Should respect min and max criticality bounds")
    void shouldRespectMinAndMaxCriticalityBounds() {
        // Given - Extreme input that would normally exceed bounds
        TestInput extremeInput = new TestInput(100.0); // Very high temperature
        
        // When
        Float result = calculator.calculate(extremeInput);

        // Then
        assertThat(result).isNotNull();
        assertThat(result).isBetween(0.0f, 1.0f); // Should be clamped to max value
        assertThat(result).isLessThanOrEqualTo(1.0f);
    }

    @Test
    @DisplayName("Should handle perfect conditions (zero deviation)")
    void shouldHandlePerfectConditions() {
        // Given - Perfect conditions matching deviation values
        TestInput perfectInput = new TestInput(20.0, 50.0); // Exactly optimal temperature and humidity
        
        // When
        Float result = calculator.calculate(perfectInput);

        // Then
        assertThat(result).isNotNull();
        assertThat(result).isEqualTo(0.0f); // Perfect conditions should result in zero criticality
    }

    @Test
    @DisplayName("Should handle more parameters than configured")
    void shouldHandleMoreParametersThanConfigured() {
        // Given - More input values than configured parameters
        TestInput inputWithExtraValues = new TestInput(25.0, 80.0, 8000.0, 15.0, 30.0);
        
        // When
        Float result = calculator.calculate(inputWithExtraValues);

        // Then
        assertThat(result).isNotNull();
        assertThat(result).isBetween(0.0f, 1.0f);
        // Should only process the first 3 parameters (temperature, humidity, visibility)
    }

    @Test
    @DisplayName("Should handle fewer parameters than input values")
    void shouldHandleFewerParametersThanInputValues() {
        // Given - Single parameter config but multiple input values
        calculator = new TestCriticalityCalculator(1); // Only temperature parameter
        TestInput input = new TestInput(25.0, 80.0, 8000.0);
        
        // When
        Float result = calculator.calculate(input);

        // Then
        assertThat(result).isNotNull();
        assertThat(result).isBetween(0.0f, 1.0f);
        // Should only process the first parameter
    }

    @Test
    @DisplayName("Should handle null input gracefully")
    void shouldHandleNullInputGracefully() {
        // When & Then
        assertThatThrownBy(() -> calculator.calculate(null))
                .isInstanceOf(NullPointerException.class);
    }

    // Test implementation classes
    private static class TestInput {
        private final Double[] values;

        public TestInput(Double... values) {
            this.values = values;
        }

        public Double[] getValues() {
            return values;
        }
    }

    private static class TestCriticalityCalculator extends AbstractCriticalityCalculator<TestInput> {
        private final int maxParameters;

        public TestCriticalityCalculator() {
            this(3); // Default: temperature, humidity, visibility
        }

        public TestCriticalityCalculator(int maxParameters) {
            this.maxParameters = maxParameters;
        }

        @Override
        protected @NotNull Double[] getNumericValues(TestInput input) {
            return input.getValues();
        }

        @Override
        protected @NotNull ConfigCriticalitySettings getCriticalitySettings() {
            List<ConfigCriticalitySettings.Parameter> parameters = List.of(
                    new ConfigCriticalitySettings.Parameter("temperature", 20.0, 0.5, 0.0, 50.0, false),
                    new ConfigCriticalitySettings.Parameter("humidity", 50.0, 0.3, 0.0, 100.0, false),
                    new ConfigCriticalitySettings.Parameter("visibility", 5000.0, 0.2, 1000.0, 10000.0, true)
            ).subList(0, Math.min(maxParameters, 3));

            return new ConfigCriticalitySettings(parameters, 0.0, 1.0);
        }
    }
}