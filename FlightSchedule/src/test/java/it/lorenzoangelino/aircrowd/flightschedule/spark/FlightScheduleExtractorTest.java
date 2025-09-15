package it.lorenzoangelino.aircrowd.flightschedule.spark;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import it.lorenzoangelino.aircrowd.flightschedule.converters.ExtensionConverter;
import it.lorenzoangelino.aircrowd.flightschedule.exceptions.FlightScheduleException;
import java.io.File;
import java.util.Optional;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.DataFrameReader;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SparkSession;
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
class FlightScheduleExtractorTest {

    @Mock
    private SparkSession mockSparkSession;

    @Mock
    private ExtensionConverter mockCsvConverter;

    @Mock
    private DataFrameReader mockDataFrameReader;

    @Mock
    private Dataset<Row> mockDataset;

    private FlightScheduleExtractor extractor;

    @BeforeEach
    void setUp() {
        extractor = new FlightScheduleExtractor(mockSparkSession, mockCsvConverter);
        
        when(mockSparkSession.read()).thenReturn(mockDataFrameReader);
        when(mockDataFrameReader.option(anyString(), anyString())).thenReturn(mockDataFrameReader);
        when(mockDataFrameReader.csv(anyString())).thenReturn(mockDataset);
    }

    @Test
    @DisplayName("Should extract data from CSV file successfully")
    void shouldExtractDataFromCsvFileSuccessfully() {
        // Given
        String csvSource = "/path/to/flights.csv";

        // When
        Dataset<Row> result = extractor.extract(csvSource);

        // Then
        assertThat(result).isNotNull().isSameAs(mockDataset);
        
        verify(mockSparkSession).read();
        verify(mockDataFrameReader).option("header", "true");
        verify(mockDataFrameReader).option("inferSchema", "true");
        verify(mockDataFrameReader).csv(csvSource);
        verifyNoInteractions(mockCsvConverter);
    }

    @Test
    @DisplayName("Should extract data from XLSX file after conversion")
    void shouldExtractDataFromXlsxFileAfterConversion() {
        // Given
        String xlsxSource = "/path/to/flights.xlsx";
        String expectedCsvPath = "/path/to/flights.csv";
        File convertedFile = new File(expectedCsvPath);

        when(mockCsvConverter.convert(xlsxSource, expectedCsvPath))
                .thenReturn(Optional.of(convertedFile));

        // When
        Dataset<Row> result = extractor.extract(xlsxSource);

        // Then
        assertThat(result).isNotNull().isSameAs(mockDataset);
        
        verify(mockCsvConverter).convert(xlsxSource, expectedCsvPath);
        verify(mockSparkSession).read();
        verify(mockDataFrameReader).option("header", "true");
        verify(mockDataFrameReader).option("inferSchema", "true");
        verify(mockDataFrameReader).csv(expectedCsvPath);
    }

    @Test
    @DisplayName("Should handle XLSX conversion failure")
    void shouldHandleXlsxConversionFailure() {
        // Given
        String xlsxSource = "/path/to/invalid.xlsx";
        String expectedCsvPath = "/path/to/invalid.csv";

        when(mockCsvConverter.convert(xlsxSource, expectedCsvPath))
                .thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> extractor.extract(xlsxSource))
                .isInstanceOf(FlightScheduleException.class)
                .hasMessageContaining("Failed to convert XLSX to CSV");

        verify(mockCsvConverter).convert(xlsxSource, expectedCsvPath);
        verify(mockSparkSession, never()).read();
    }

    @Test
    @DisplayName("Should handle unsupported file format")
    void shouldHandleUnsupportedFileFormat() {
        // Given
        String unsupportedSource = "/path/to/flights.json";

        // When & Then
        assertThatThrownBy(() -> extractor.extract(unsupportedSource))
                .isInstanceOf(FlightScheduleException.class)
                .hasMessageContaining("Unsupported file format");

        verifyNoInteractions(mockCsvConverter);
        verify(mockSparkSession, never()).read();
    }

    @Test
    @DisplayName("Should handle Spark reading exceptions")
    void shouldHandleSparkReadingExceptions() {
        // Given
        String csvSource = "/path/to/corrupted.csv";

        when(mockDataFrameReader.csv(csvSource))
                .thenThrow(new RuntimeException("Spark read error"));

        // When & Then
        assertThatThrownBy(() -> extractor.extract(csvSource))
                .isInstanceOf(FlightScheduleException.class)
                .hasMessageContaining("Data extraction failed")
                .hasCauseInstanceOf(RuntimeException.class);

        verify(mockSparkSession).read();
        verify(mockDataFrameReader).csv(csvSource);
    }

    @Test
    @DisplayName("Should validate file extensions correctly")
    void shouldValidateFileExtensionsCorrectly() {
        // Test different file extensions
        String[] validCsvFiles = {
            "/path/file.csv",
            "/another/path/data.CSV",
            "simple.csv"
        };

        String[] validXlsxFiles = {
            "/path/file.xlsx", 
            "/another/path/data.XLSX",
            "simple.xlsx"
        };

        // CSV files should work directly
        for (String csvFile : validCsvFiles) {
            assertThatCode(() -> extractor.extract(csvFile)).doesNotThrowAnyException();
        }

        // XLSX files should attempt conversion
        File mockFile = new File("/converted.csv");
        when(mockCsvConverter.convert(anyString(), anyString()))
                .thenReturn(Optional.of(mockFile));

        for (String xlsxFile : validXlsxFiles) {
            assertThatCode(() -> extractor.extract(xlsxFile)).doesNotThrowAnyException();
        }
    }
}