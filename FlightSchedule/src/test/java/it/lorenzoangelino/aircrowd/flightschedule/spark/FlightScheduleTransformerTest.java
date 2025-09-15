package it.lorenzoangelino.aircrowd.flightschedule.spark;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import org.apache.spark.sql.Column;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
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
class FlightScheduleTransformerTest {

    @Mock
    private Dataset<Row> mockDataset;

    private FlightScheduleTransformer transformer;

    @BeforeEach
    void setUp() {
        // Initialize transformer with both configuration options
        transformer = new FlightScheduleTransformer(true, true);
        
        // Mock dataset operations to return self for chaining
        when(mockDataset.drop(anyString())).thenReturn(mockDataset);
        when(mockDataset.withColumnRenamed(anyString(), anyString())).thenReturn(mockDataset);
        when(mockDataset.orderBy(any(Column.class), any(Column.class), any(Column.class), any(Column.class))).thenReturn(mockDataset);
        when(mockDataset.withColumn(anyString(), any(Column.class))).thenReturn(mockDataset);
        when(mockDataset.filter(anyString())).thenReturn(mockDataset);
        when(mockDataset.groupBy(anyString(), anyString())).thenReturn(mock(org.apache.spark.sql.RelationalGroupedDataset.class));
        when(mockDataset.join(any(Dataset.class), any(Column.class), anyString())).thenReturn(mockDataset);
        when(mockDataset.columns()).thenReturn(new String[]{"DATA", "FASCIA_ORARIA", "CODICE", "seats", "GIORNO_NOME"});
    }

    @Test
    @DisplayName("Should execute transformation pipeline")
    void shouldExecuteTransformationPipeline() {
        // When
        Dataset<Row> result = transformer.transform(mockDataset);

        // Then
        assertThat(result).isNotNull().isSameAs(mockDataset);
        
        // Verify key transformation steps
        verify(mockDataset).drop("PARTENZA_ARRIVO");
        verify(mockDataset).withColumnRenamed("SEATS", "seats");
        verify(mockDataset, atLeastOnce()).withColumn(anyString(), any(Column.class));
    }

    @Test
    @DisplayName("Should execute full transformation pipeline")
    void shouldExecuteFullTransformationPipeline() {
        // Given
        when(mockDataset.first()).thenReturn(mock(Row.class));
        when(mockDataset.first().getString(0)).thenReturn("15/01/2025", "LUNEDÌ");

        // When
        Dataset<Row> result = transformer.transform(mockDataset);

        // Then
        assertThat(result).isNotNull().isSameAs(mockDataset);
        
        // Verify key transformation steps
        verify(mockDataset).drop("PARTENZA_ARRIVO"); // Remove unused columns
        verify(mockDataset).withColumnRenamed("SEATS", "seats"); // Rename columns
        verify(mockDataset).orderBy(anyString(), anyString(), anyString(), anyString()); // Sort flights
        verify(mockDataset, atLeastOnce()).withColumn(anyString(), any(Column.class)); // Multiple column operations
    }

    @Test
    @DisplayName("Should handle flight type parsing correctly")
    void shouldHandleFlightTypeParsingCorrectly() {
        // When
        transformer.transform(mockDataset);

        // Then
        verify(mockDataset).withColumn(eq("type"), any(Column.class));
        verify(mockDataset).drop("CODICE");
    }

    @Test
    @DisplayName("Should handle date and time combination")
    void shouldHandleDateAndTimeCombination() {
        // When
        transformer.transform(mockDataset);

        // Then
        verify(mockDataset).withColumn(eq("datetime"), any(Column.class));
        verify(mockDataset).drop("DATE");
        verify(mockDataset).drop("FASCIA_ORARIA");
    }

    @Test
    @DisplayName("Should shift dates to today when enabled")
    void shouldShiftDatesToTodayWhenEnabled() {
        // Given
        Row mockRow = mock(Row.class);
        when(mockDataset.select("DATA")).thenReturn(mockDataset);
        when(mockDataset.select("GIORNO_NOME")).thenReturn(mockDataset);
        when(mockDataset.first()).thenReturn(mockRow);
        when(mockRow.getString(0)).thenReturn("15/01/2025", "LUNEDÌ");

        // When
        transformer.transform(mockDataset);

        // Then
        verify(mockDataset, atLeastOnce()).select("DATA");
        verify(mockDataset, atLeastOnce()).select("GIORNO_NOME");
    }

    @Test
    @DisplayName("Should remove day name when enabled")
    void shouldRemoveDayNameWhenEnabled() {
        // Given
        Row mockRow = mock(Row.class);
        when(mockDataset.select(anyString())).thenReturn(mockDataset);
        when(mockDataset.first()).thenReturn(mockRow);
        when(mockRow.getString(0)).thenReturn("15/01/2025", "LUNEDÌ");

        // When
        transformer.transform(mockDataset);

        // Then
        verify(mockDataset).drop("GIORNO_NOME");
    }

    @Test
    @DisplayName("Should handle transformer without date shifting")
    void shouldHandleTransformerWithoutDateShifting() {
        // Given
        FlightScheduleTransformer noShiftTransformer = new FlightScheduleTransformer(false, false);

        // When
        Dataset<Row> result = noShiftTransformer.transform(mockDataset);

        // Then
        assertThat(result).isNotNull().isSameAs(mockDataset);
        // Should not call date-related selects when shifting is disabled
        verify(mockDataset, never()).select("DATA");
    }

    @Test
    @DisplayName("Should update seats with average values")
    void shouldUpdateSeatsWithAverageValues() {
        // Given
        org.apache.spark.sql.RelationalGroupedDataset mockGroupedDataset = 
            mock(org.apache.spark.sql.RelationalGroupedDataset.class);
        when(mockDataset.filter("SEATS > 0")).thenReturn(mockDataset);
        when(mockDataset.groupBy("GIORNO_NOME", "FASCIA_ORARIA")).thenReturn(mockGroupedDataset);
        when(mockGroupedDataset.agg(any(Column.class))).thenReturn(mockDataset);

        // When
        transformer.transform(mockDataset);

        // Then
        verify(mockDataset).filter("SEATS > 0");
        verify(mockDataset).groupBy("GIORNO_NOME", "FASCIA_ORARIA");
        verify(mockDataset).join(any(Dataset.class), any(Column.class), eq("left"));
    }

    @Test
    @DisplayName("Should generate flight codes when column missing")
    void shouldGenerateFlightCodesWhenColumnMissing() {
        // Given
        when(mockDataset.columns()).thenReturn(new String[]{"DATA", "FASCIA_ORARIA"});

        // When
        transformer.transform(mockDataset);

        // Then
        verify(mockDataset).withColumn(eq("code"), any(Column.class));
    }

    @Test
    @DisplayName("Should validate day of week conversion")
    void shouldValidateDayOfWeekConversion() {
        // This test validates the private method logic through reflection or by testing the overall behavior
        // Given different day names, the transformer should handle them correctly
        
        // Test that the transformer doesn't throw exceptions for valid Italian day names
        Row mockRow = mock(Row.class);
        when(mockDataset.select(anyString())).thenReturn(mockDataset);
        when(mockDataset.first()).thenReturn(mockRow);
        when(mockRow.getString(0)).thenReturn("15/01/2025", "MERCOLEDÌ");

        // When & Then
        assertThatCode(() -> transformer.transform(mockDataset))
                .doesNotThrowAnyException();
    }
}