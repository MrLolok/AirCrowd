package it.lorenzoangelino.aircrowd.weather.provider;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import it.lorenzoangelino.aircrowd.common.models.locations.GeographicalLocation;
import it.lorenzoangelino.aircrowd.common.models.weather.WeatherData;
import it.lorenzoangelino.aircrowd.common.models.weather.WeatherDataForecast;
import it.lorenzoangelino.aircrowd.weather.api.callbacks.WeatherForecastCallback;
import it.lorenzoangelino.aircrowd.weather.api.clients.APIClientRequester;
import it.lorenzoangelino.aircrowd.weather.api.params.QueryParam;
import it.lorenzoangelino.aircrowd.weather.api.responses.WeatherForecastResponse;
import it.lorenzoangelino.aircrowd.weather.retry.RetryPolicy;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class WeatherDataProviderTest {

    @Mock
    private APIClientRequester mockRequester;

    @Mock
    private RetryPolicy mockRetryPolicy;

    private WeatherDataProviderImpl weatherDataProvider;
    private GeographicalLocation testLocation;

    @BeforeEach
    void setUp() {
        testLocation = new GeographicalLocation("Test Airport", 40.88, 14.28);
        weatherDataProvider = new WeatherDataProviderImpl(mockRequester, mockRetryPolicy);
    }

    @Nested
    @DisplayName("Weather Forecast Fetching Tests")
    class WeatherForecastFetchingTests {

        @Test
        @DisplayName("Should fetch weather forecast successfully")
        void shouldFetchWeatherForecastSuccessfully() throws Exception {
            // Given
            WeatherForecastResponse mockResponse = createMockWeatherResponse();

            when(mockRetryPolicy.execute(any(), anyString())).thenAnswer(invocation -> {
                // Simulate successful execution
                return CompletableFuture.completedFuture(new WeatherDataForecast(List.of()));
            });

            // When
            CompletableFuture<WeatherDataForecast> future = weatherDataProvider.fetchWeatherDataForecast(testLocation);
            WeatherDataForecast result = future.get();

            // Then
            assertThat(result).isNotNull();
            verify(mockRetryPolicy).execute(any(), contains("fetchWeatherDataForecast"));
        }

        @Test
        @DisplayName("Should fail when location is null")
        void shouldFailWhenLocationIsNull() {
            // When
            CompletableFuture<WeatherDataForecast> future = weatherDataProvider.fetchWeatherDataForecast(null);

            // Then
            assertThatThrownBy(future::get)
                    .isInstanceOf(ExecutionException.class)
                    .hasCauseInstanceOf(WeatherServiceException.ConfigurationException.class)
                    .hasMessageContaining("Geographical location cannot be null");
        }

        @Test
        @DisplayName("Should handle API errors with retry")
        void shouldHandleApiErrorsWithRetry() {
            // Given
            when(mockRetryPolicy.execute(any(), anyString()))
                    .thenReturn(CompletableFuture.failedFuture(
                            new WeatherServiceException.APIException("API unavailable")));

            // When
            CompletableFuture<WeatherDataForecast> future = weatherDataProvider.fetchWeatherDataForecast(testLocation);

            // Then
            assertThatThrownBy(future::get)
                    .isInstanceOf(ExecutionException.class)
                    .hasCauseInstanceOf(WeatherServiceException.APIException.class)
                    .hasMessageContaining("API unavailable");
        }
    }

    @Nested
    @DisplayName("Specific Weather Data Fetching Tests")
    class SpecificWeatherDataFetchingTests {

        @Test
        @DisplayName("Should fetch weather data for specific date")
        void shouldFetchWeatherDataForSpecificDate() throws Exception {
            // Given
            LocalDateTime targetDate = LocalDateTime.of(2024, 1, 15, 12, 0);
            WeatherData mockWeatherData = createMockWeatherData(targetDate);
            WeatherDataForecast mockForecast = new WeatherDataForecast(List.of(mockWeatherData));

            when(mockRetryPolicy.execute(any(), anyString()))
                    .thenReturn(CompletableFuture.completedFuture(mockForecast));

            // When
            CompletableFuture<WeatherData> future = weatherDataProvider.fetchWeatherData(testLocation, targetDate);
            WeatherData result = future.get();

            // Then
            assertThat(result).isNotNull();
            assertThat(result.datetime()).isEqualTo(targetDate);
        }

        @Test
        @DisplayName("Should fail when date is null")
        void shouldFailWhenDateIsNull() {
            // When
            CompletableFuture<WeatherData> future = weatherDataProvider.fetchWeatherData(testLocation, null);

            // Then
            assertThatThrownBy(future::get)
                    .isInstanceOf(ExecutionException.class)
                    .hasCauseInstanceOf(WeatherServiceException.ConfigurationException.class)
                    .hasMessageContaining("Date cannot be null");
        }

        @Test
        @DisplayName("Should fail when no matching date found")
        void shouldFailWhenNoMatchingDateFound() {
            // Given
            LocalDateTime targetDate = LocalDateTime.of(2024, 1, 15, 12, 0);
            LocalDateTime differentDate = LocalDateTime.of(2024, 1, 16, 12, 0);
            WeatherData mockWeatherData = createMockWeatherData(differentDate);
            WeatherDataForecast mockForecast = new WeatherDataForecast(List.of(mockWeatherData));

            when(mockRetryPolicy.execute(any(), anyString()))
                    .thenReturn(CompletableFuture.completedFuture(mockForecast));

            // When
            CompletableFuture<WeatherData> future = weatherDataProvider.fetchWeatherData(testLocation, targetDate);

            // Then
            assertThatThrownBy(future::get)
                    .isInstanceOf(ExecutionException.class)
                    .hasCauseInstanceOf(WeatherServiceException.DataProcessingException.class)
                    .hasMessageContaining("No weather data found for the specified date");
        }
    }

    @Nested
    @DisplayName("Error Handling Tests")
    class ErrorHandlingTests {

        @Test
        @DisplayName("Should handle null API response")
        void shouldHandleNullApiResponse() {
            // Given
            doAnswer(invocation -> {
                        WeatherForecastCallback callback = invocation.getArgument(0);
                        callback.accept(null); // Simulate null response
                        return null;
                    })
                    .when(mockRequester)
                    .get(any(WeatherForecastCallback.class), any(QueryParam.class), any(QueryParam.class));

            when(mockRetryPolicy.execute(any(), anyString())).thenAnswer(invocation -> invocation
                    .getArgument(0, java.util.function.Supplier.class)
                    .get());

            // When
            CompletableFuture<WeatherDataForecast> future = weatherDataProvider.fetchWeatherDataForecast(testLocation);

            // Then
            assertThatThrownBy(future::get)
                    .isInstanceOf(ExecutionException.class)
                    .hasCauseInstanceOf(WeatherServiceException.APIException.class)
                    .hasMessageContaining("Received null response");
        }

        @Test
        @DisplayName("Should handle malformed API response")
        void shouldHandleMalformedApiResponse() {
            // Given
            WeatherForecastResponse malformedResponse = mock(WeatherForecastResponse.class);
            when(malformedResponse.hourly()).thenReturn(null);

            doAnswer(invocation -> {
                        WeatherForecastCallback callback = invocation.getArgument(0);
                        callback.accept(malformedResponse);
                        return null;
                    })
                    .when(mockRequester)
                    .get(any(WeatherForecastCallback.class), any(QueryParam.class), any(QueryParam.class));

            when(mockRetryPolicy.execute(any(), anyString())).thenAnswer(invocation -> invocation
                    .getArgument(0, java.util.function.Supplier.class)
                    .get());

            // When
            CompletableFuture<WeatherDataForecast> future = weatherDataProvider.fetchWeatherDataForecast(testLocation);

            // Then
            assertThatThrownBy(future::get)
                    .isInstanceOf(ExecutionException.class)
                    .hasCauseInstanceOf(WeatherServiceException.DataProcessingException.class)
                    .hasMessageContaining("Invalid response structure");
        }
    }

    private WeatherForecastResponse createMockWeatherResponse() {
        WeatherForecastResponse.HourlyData hourlyData = mock(WeatherForecastResponse.HourlyData.class);
        when(hourlyData.time()).thenReturn(List.of("2024-01-15T12:00:00"));
        when(hourlyData.temperature()).thenReturn(List.of(15.5));
        when(hourlyData.relativeHumidity()).thenReturn(List.of(65.0));
        when(hourlyData.dewPoint()).thenReturn(List.of(10.0));
        when(hourlyData.precipitationProbability()).thenReturn(List.of(20.0));
        when(hourlyData.rain()).thenReturn(List.of(0.0));
        when(hourlyData.showers()).thenReturn(List.of(0.0));
        when(hourlyData.snowfall()).thenReturn(List.of(0.0));
        when(hourlyData.pressure()).thenReturn(List.of(1013.25));
        when(hourlyData.cloudCover()).thenReturn(List.of(50.0));
        when(hourlyData.visibility()).thenReturn(List.of(10000.0));
        when(hourlyData.windSpeed()).thenReturn(List.of(5.5));
        when(hourlyData.windDirection()).thenReturn(List.of(180.0));

        WeatherForecastResponse response = mock(WeatherForecastResponse.class);
        when(response.hourly()).thenReturn(hourlyData);
        return response;
    }

    private WeatherData createMockWeatherData(LocalDateTime dateTime) {
        return new WeatherData(
                testLocation,
                LocalDateTime.now(),
                dateTime,
                15.5,
                65.0,
                10.0,
                20.0,
                0.0,
                0.0,
                0.0,
                1013.25,
                50.0,
                10000.0,
                5.5,
                180.0);
    }
}
