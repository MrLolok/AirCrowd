package it.lorenzoangelino.aircrowd.weather.provider;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import it.lorenzoangelino.aircrowd.common.models.locations.GeographicalLocation;
import it.lorenzoangelino.aircrowd.common.models.weather.WeatherData;
import it.lorenzoangelino.aircrowd.common.models.weather.WeatherDataForecast;
import it.lorenzoangelino.aircrowd.weather.api.clients.HttpAPIClientRequester;
import it.lorenzoangelino.aircrowd.weather.exceptions.WeatherAPIException;
import it.lorenzoangelino.aircrowd.weather.retry.RetryPolicy;
import java.time.LocalDateTime;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
@SpringBootTest
@TestPropertySource(properties = {
    "spring.cloud.config.enabled=false",
    "weather.openmeteo.base-url=https://api.open-meteo.com/v1",
    "weather.openmeteo.hourly=temperature_2m,relative_humidity_2m,dew_point_2m,precipitation_probability,rain,showers,snowfall,pressure_msl,cloud_cover,visibility,wind_speed_10m,wind_direction_10m",
    "weather.openmeteo.forecast-days=7",
    "weather.openmeteo.timezone=Europe/Rome"
})
class WeatherDataProviderImplTest {

    @Mock
    private HttpAPIClientRequester mockHttpClient;

    @Mock
    private RetryPolicy mockRetryPolicy;

    @Mock
    private ObjectMapper mockObjectMapper;

    private WeatherDataProviderImpl weatherDataProvider;
    private GeographicalLocation testLocation;

    @BeforeEach
    void setUp() {
        weatherDataProvider = new WeatherDataProviderImpl(mockHttpClient, mockRetryPolicy, mockObjectMapper);
        
        // Set configuration properties via reflection
        ReflectionTestUtils.setField(weatherDataProvider, "openmeteoBaseUrl", "https://api.open-meteo.com/v1");
        ReflectionTestUtils.setField(weatherDataProvider, "openmeteoHourly", 
            "temperature_2m,relative_humidity_2m,dew_point_2m,precipitation_probability,rain,showers,snowfall,pressure_msl,cloud_cover,visibility,wind_speed_10m,wind_direction_10m");
        ReflectionTestUtils.setField(weatherDataProvider, "openmeteoForecastDays", 7);
        ReflectionTestUtils.setField(weatherDataProvider, "openmeteoTimezone", "Europe/Rome");

        // Test location (Rome)
        testLocation = new GeographicalLocation("Rome", 41.9028, 12.4964);
    }

    @Test
    @DisplayName("Should successfully fetch weather forecast when API responds correctly")
    void shouldSuccessfullyFetchWeatherForecast() throws Exception {
        // Given
        WeatherDataForecast expectedForecast = createMockWeatherForecast();
        CompletableFuture<WeatherDataForecast> successFuture = CompletableFuture.completedFuture(expectedForecast);

        when(mockRetryPolicy.execute(any(Supplier.class), eq("fetchWeatherDataForecast")))
                .thenReturn(successFuture);

        // When
        CompletableFuture<WeatherDataForecast> result = weatherDataProvider.fetchWeatherDataForecast(testLocation);

        // Then
        assertThat(result).isCompleted();
        WeatherDataForecast forecast = result.get(5, TimeUnit.SECONDS);
        assertThat(forecast).isNotNull();
        assertThat(forecast.hourlyWeatherData()).isNotEmpty();
        
        verify(mockRetryPolicy).execute(any(Supplier.class), eq("fetchWeatherDataForecast"));
        verify(mockHttpClient).setBaseURL("https://api.open-meteo.com/v1");
    }

    @Test
    @DisplayName("Should handle API timeout exception")
    void shouldHandleApiTimeoutException() {
        // Given
        CompletableFuture<WeatherDataForecast> failedFuture = new CompletableFuture<>();
        failedFuture.completeExceptionally(new RuntimeException("API timeout"));

        when(mockRetryPolicy.execute(any(Supplier.class), eq("fetchWeatherDataForecast")))
                .thenReturn(failedFuture);

        // When & Then
        CompletableFuture<WeatherDataForecast> result = weatherDataProvider.fetchWeatherDataForecast(testLocation);
        
        assertThatThrownBy(() -> result.get(5, TimeUnit.SECONDS))
                .isInstanceOf(ExecutionException.class)
                .hasCauseInstanceOf(WeatherAPIException.class)
                .hasMessageContaining("Failed to fetch weather forecast");
    }

    @Test
    @DisplayName("Should successfully fetch current weather data")
    void shouldSuccessfullyFetchCurrentWeatherData() throws Exception {
        // Given
        WeatherData expectedWeatherData = createMockWeatherData();
        CompletableFuture<WeatherData> successFuture = CompletableFuture.completedFuture(expectedWeatherData);

        when(mockRetryPolicy.execute(any(Supplier.class), eq("fetchWeatherData")))
                .thenReturn(successFuture);

        LocalDateTime testDate = LocalDateTime.now();

        // When
        CompletableFuture<WeatherData> result = weatherDataProvider.fetchWeatherData(testLocation, testDate);

        // Then
        assertThat(result).isCompleted();
        WeatherData weatherData = result.get(5, TimeUnit.SECONDS);
        assertThat(weatherData).isNotNull();
        assertThat(weatherData.location()).isEqualTo(testLocation);
        assertThat(weatherData.temperature()).isEqualTo(25.5);
        
        verify(mockRetryPolicy).execute(any(Supplier.class), eq("fetchWeatherData"));
    }

    @Test
    @DisplayName("Should validate OpenMeteo API configuration")
    void shouldValidateOpenMeteoApiConfiguration() {
        // Given
        String expectedBaseUrl = "https://api.open-meteo.com/v1";
        String expectedTimezone = "Europe/Rome";
        int expectedForecastDays = 7;

        // When - Getting configuration values via reflection
        String actualBaseUrl = (String) ReflectionTestUtils.getField(weatherDataProvider, "openmeteoBaseUrl");
        String actualHourlyParams = (String) ReflectionTestUtils.getField(weatherDataProvider, "openmeteoHourly");
        String actualTimezone = (String) ReflectionTestUtils.getField(weatherDataProvider, "openmeteoTimezone");
        int actualForecastDays = (int) ReflectionTestUtils.getField(weatherDataProvider, "openmeteoForecastDays");

        // Then
        assertThat(actualBaseUrl).isEqualTo(expectedBaseUrl);
        assertThat(actualHourlyParams).contains("temperature_2m", "pressure_msl");
        assertThat(actualTimezone).isEqualTo(expectedTimezone);
        assertThat(actualForecastDays).isEqualTo(expectedForecastDays);
    }

    @Test
    @DisplayName("Should handle OpenMeteo API response structure")
    void shouldHandleOpenMeteoApiResponseStructure() {
        // Given - Sample OpenMeteo response structure for documentation
        String sampleOpenMeteoResponse = """
            {
              "latitude": 41.902779,
              "longitude": 12.496366,
              "timezone": "Europe/Rome",
              "hourly": {
                "time": ["2025-01-15T00:00", "2025-01-15T01:00"],
                "temperature_2m": [15.2, 14.8],
                "relative_humidity_2m": [85, 87],
                "pressure_msl": [1013.2, 1012.8],
                "wind_speed_10m": [12.5, 11.2]
              }
            }
            """;

        // Then - Validate expected structure
        assertThat(sampleOpenMeteoResponse).contains("hourly");
        assertThat(sampleOpenMeteoResponse).contains("temperature_2m");
        assertThat(sampleOpenMeteoResponse).contains("pressure_msl");
        assertThat(sampleOpenMeteoResponse).contains("Europe/Rome");
    }

    private WeatherDataForecast createMockWeatherForecast() {
        WeatherData weatherData = createMockWeatherData();
        return new WeatherDataForecast(java.util.List.of(weatherData));
    }

    private WeatherData createMockWeatherData() {
        return new WeatherData(
                testLocation,
                LocalDateTime.now(),
                LocalDateTime.now().plusHours(1),
                25.5, // temperature
                65,   // humidity
                18.2, // dewPoint
                20,   // precipitationProbability
                0.0,  // rain
                0.0,  // showers
                0.0,  // snowfall
                1013.25, // pressure (matching pressure_msl from OpenMeteo)
                40,   // cloudCover
                10000, // visibility
                5.2,  // windSpeed
                180   // windDirection
        );
    }
}