package it.lorenzoangelino.aircrowd.flightschedule.spark;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import it.lorenzoangelino.aircrowd.flightschedule.exceptions.FlightScheduleException;
import org.apache.spark.sql.DataFrameWriter;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SaveMode;
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
class FlightScheduleLoaderTest {

    @Mock
    private Dataset<Row> mockDataset;

    @Mock
    private DataFrameWriter<Row> mockWriter;

    private FlightScheduleLoader loader;

    @BeforeEach
    void setUp() {
        loader = new FlightScheduleLoader();
        
        when(mockDataset.write()).thenReturn(mockWriter);
        when(mockWriter.mode(any(SaveMode.class))).thenReturn(mockWriter);
        when(mockWriter.option(anyString(), anyString())).thenReturn(mockWriter);
        when(mockDataset.count()).thenReturn(150L);
    }

    @Test
    @DisplayName("Should load data to CSV destination successfully")
    void shouldLoadDataToCsvDestinationSuccessfully() {
        // Given
        String destination = "/path/to/output";

        // When
        assertThatCode(() -> loader.load(mockDataset, destination))
                .doesNotThrowAnyException();

        // Then
        verify(mockDataset).write();
        verify(mockWriter).mode(SaveMode.Overwrite);
        verify(mockWriter).option("header", "true");
        verify(mockWriter).csv(destination);
        verify(mockDataset).count(); // For logging
    }

    @Test
    @DisplayName("Should handle empty dataset gracefully")
    void shouldHandleEmptyDatasetGracefully() {
        // Given
        String destination = "/path/to/output";
        when(mockDataset.count()).thenReturn(0L);

        // When
        assertThatCode(() -> loader.load(mockDataset, destination))
                .doesNotThrowAnyException();

        // Then
        verify(mockDataset).write();
        verify(mockWriter).mode(SaveMode.Overwrite);
        verify(mockWriter).option("header", "true");
        verify(mockWriter).csv(destination);
    }

    @Test
    @DisplayName("Should handle Spark write exceptions")
    void shouldHandleSparkWriteExceptions() {
        // Given
        String destination = "/invalid/path/output";

        doThrow(new RuntimeException("Permission denied"))
                .when(mockWriter).csv(destination);

        // When & Then
        assertThatThrownBy(() -> loader.load(mockDataset, destination))
                .isInstanceOf(FlightScheduleException.class)
                .hasMessageContaining("Data loading failed")
                .hasCauseInstanceOf(RuntimeException.class);

        verify(mockDataset).write();
        verify(mockWriter).mode(SaveMode.Overwrite);
        verify(mockWriter).option("header", "true");
        verify(mockWriter).csv(destination);
    }

    @Test
    @DisplayName("Should use correct save mode and options")
    void shouldUseCorrectSaveModeAndOptions() {
        // Given
        String destination = "/path/to/flights_output";

        // When
        loader.load(mockDataset, destination);

        // Then - Verify the exact sequence and parameters
        verify(mockDataset).write();
        verify(mockWriter).mode(SaveMode.Overwrite);
        verify(mockWriter).option("header", "true");
        verify(mockWriter).csv(destination);
    }

    @Test
    @DisplayName("Should handle large dataset loading")
    void shouldHandleLargeDatasetLoading() {
        // Given
        String destination = "/path/to/large_output";
        when(mockDataset.count()).thenReturn(1_000_000L);

        // When
        assertThatCode(() -> loader.load(mockDataset, destination))
                .doesNotThrowAnyException();

        // Then
        verify(mockDataset).write();
        verify(mockWriter).csv(destination);
        verify(mockDataset).count(); // Should log the large count
    }

    @Test
    @DisplayName("Should handle null destination gracefully")
    void shouldHandleNullDestinationGracefully() {
        // Given
        String destination = null;

        // When & Then
        assertThatThrownBy(() -> loader.load(mockDataset, destination))
                .isInstanceOf(FlightScheduleException.class)
                .hasMessageContaining("Data loading failed");
    }
}