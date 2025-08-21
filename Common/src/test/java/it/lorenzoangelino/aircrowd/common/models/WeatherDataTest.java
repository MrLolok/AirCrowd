package it.lorenzoangelino.aircrowd.common.models;

import static org.assertj.core.api.Assertions.*;

import it.lorenzoangelino.aircrowd.common.models.locations.GeographicalLocation;
import it.lorenzoangelino.aircrowd.common.models.weather.WeatherData;
import java.time.LocalDateTime;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class WeatherDataTest {

    @Nested
    @DisplayName("WeatherData Creation Tests")
    class WeatherDataCreationTests {

        @Test
        @DisplayName("Should create WeatherData with valid parameters")
        void shouldCreateWeatherDataWithValidParameters() {
            // Given
            String id = "weather-001";
            GeographicalLocation location = new GeographicalLocation("Airport", 40.88, 14.28);
            LocalDateTime timestamp = LocalDateTime.of(2024, 1, 15, 12, 0);
            double temperature = 15.5;
            double humidity = 65.0;
            double windSpeed = 10.2;
            String conditions = "Cloudy";

            // When
            WeatherData weatherData =
                    new WeatherData(id, location, timestamp, temperature, humidity, windSpeed, conditions);

            // Then
            assertThat(weatherData).isNotNull();
            assertThat(weatherData.id()).isEqualTo(id);
            assertThat(weatherData.location()).isEqualTo(location);
            assertThat(weatherData.timestamp()).isEqualTo(timestamp);
            assertThat(weatherData.temperature()).isEqualTo(temperature);
            assertThat(weatherData.humidity()).isEqualTo(humidity);
            assertThat(weatherData.windSpeed()).isEqualTo(windSpeed);
            assertThat(weatherData.conditions()).isEqualTo(conditions);
        }

        @Test
        @DisplayName("Should handle negative temperature")
        void shouldHandleNegativeTemperature() {
            // Given
            String id = "weather-002";
            GeographicalLocation location = new GeographicalLocation("Airport", 40.88, 14.28);
            LocalDateTime timestamp = LocalDateTime.now();
            double temperature = -5.0;
            double humidity = 80.0;
            double windSpeed = 15.0;
            String conditions = "Snow";

            // When
            WeatherData weatherData =
                    new WeatherData(id, location, timestamp, temperature, humidity, windSpeed, conditions);

            // Then
            assertThat(weatherData.temperature()).isEqualTo(temperature);
        }

        @Test
        @DisplayName("Should handle zero wind speed")
        void shouldHandleZeroWindSpeed() {
            // Given
            String id = "weather-003";
            GeographicalLocation location = new GeographicalLocation("Airport", 40.88, 14.28);
            LocalDateTime timestamp = LocalDateTime.now();
            double temperature = 20.0;
            double humidity = 50.0;
            double windSpeed = 0.0;
            String conditions = "Calm";

            // When
            WeatherData weatherData =
                    new WeatherData(id, location, timestamp, temperature, humidity, windSpeed, conditions);

            // Then
            assertThat(weatherData.windSpeed()).isEqualTo(windSpeed);
        }
    }

    @Nested
    @DisplayName("WeatherData Equality Tests")
    class WeatherDataEqualityTests {

        @Test
        @DisplayName("Should be equal when all fields are the same")
        void shouldBeEqualWhenAllFieldsAreTheSame() {
            // Given
            String id = "weather-001";
            GeographicalLocation location = new GeographicalLocation("Airport", 40.88, 14.28);
            LocalDateTime timestamp = LocalDateTime.of(2024, 1, 15, 12, 0);
            double temperature = 15.5;
            double humidity = 65.0;
            double windSpeed = 10.2;
            String conditions = "Cloudy";

            WeatherData weatherData1 =
                    new WeatherData(id, location, timestamp, temperature, humidity, windSpeed, conditions);
            WeatherData weatherData2 =
                    new WeatherData(id, location, timestamp, temperature, humidity, windSpeed, conditions);

            // When & Then
            assertThat(weatherData1).isEqualTo(weatherData2);
            assertThat(weatherData1.hashCode()).isEqualTo(weatherData2.hashCode());
        }

        @Test
        @DisplayName("Should not be equal when IDs differ")
        void shouldNotBeEqualWhenIdsDiffer() {
            // Given
            GeographicalLocation location = new GeographicalLocation("Airport", 40.88, 14.28);
            LocalDateTime timestamp = LocalDateTime.of(2024, 1, 15, 12, 0);
            double temperature = 15.5;
            double humidity = 65.0;
            double windSpeed = 10.2;
            String conditions = "Cloudy";

            WeatherData weatherData1 =
                    new WeatherData("weather-001", location, timestamp, temperature, humidity, windSpeed, conditions);
            WeatherData weatherData2 =
                    new WeatherData("weather-002", location, timestamp, temperature, humidity, windSpeed, conditions);

            // When & Then
            assertThat(weatherData1).isNotEqualTo(weatherData2);
        }
    }

    @Nested
    @DisplayName("WeatherData String Representation Tests")
    class WeatherDataStringRepresentationTests {

        @Test
        @DisplayName("Should have meaningful toString representation")
        void shouldHaveMeaningfulToStringRepresentation() {
            // Given
            String id = "weather-001";
            GeographicalLocation location = new GeographicalLocation("Test Airport", 40.88, 14.28);
            LocalDateTime timestamp = LocalDateTime.of(2024, 1, 15, 12, 0);
            double temperature = 15.5;
            double humidity = 65.0;
            double windSpeed = 10.2;
            String conditions = "Cloudy";

            WeatherData weatherData =
                    new WeatherData(id, location, timestamp, temperature, humidity, windSpeed, conditions);

            // When
            String toString = weatherData.toString();

            // Then
            assertThat(toString)
                    .contains(id)
                    .contains("Test Airport")
                    .contains("15.5")
                    .contains("65.0")
                    .contains("10.2")
                    .contains("Cloudy");
        }
    }
}
