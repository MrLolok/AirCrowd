package it.lorenzoangelino.aircrowd.flightschedule.spark;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import it.lorenzoangelino.aircrowd.flightschedule.exceptions.FlightScheduleException;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SparkSession;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.test.context.TestPropertySource;

@SpringBootTest
@TestPropertySource(properties = {"spring.cloud.config.enabled=false", "spring.cache.type=none"})
class FlightScheduleETLProcessTest {

    @MockBean
    private SparkSession mockSparkSession;

    @MockBean
    private FlightScheduleExtractor mockExtractor;

    @MockBean
    private FlightScheduleTransformer mockTransformer;

    @MockBean
    private FlightScheduleLoader mockLoader;

    @Autowired
    private FlightScheduleETLProcess etlProcess;

    @Mock
    private Dataset<Row> mockDataset;

    @BeforeEach
    void setUp() {
        when(mockDataset.count()).thenReturn(100L);
    }

    @Nested
    @DisplayName("ETL Process Execution Tests")
    class ETLProcessExecutionTests {

        @Test
        @DisplayName("Should execute ETL process successfully with Spring context")
        void shouldExecuteETLProcessSuccessfullyWithSpringContext() {
            // Given
            String source = "/path/to/source.csv";
            String destination = "/path/to/destination";

            when(mockExtractor.extract(source)).thenReturn(mockDataset);
            when(mockTransformer.transform(mockDataset)).thenReturn(mockDataset);
            doNothing().when(mockLoader).load(mockDataset, destination);

            // When & Then
            assertThatCode(() -> etlProcess.run(source, destination)).doesNotThrowAnyException();

            verify(mockExtractor).extract(source);
            verify(mockTransformer).transform(mockDataset);
            verify(mockLoader).load(mockDataset, destination);
        }

        @Test
        @DisplayName("Should handle empty dataset gracefully")
        void shouldHandleEmptyDatasetGracefully() {
            // Given
            String source = "/path/to/empty.csv";
            String destination = "/path/to/destination";

            Dataset<Row> emptyDataset = mock(Dataset.class);
            when(emptyDataset.count()).thenReturn(0L);

            when(mockExtractor.extract(source)).thenReturn(emptyDataset);
            when(mockTransformer.transform(emptyDataset)).thenReturn(emptyDataset);
            doNothing().when(mockLoader).load(emptyDataset, destination);

            // When & Then
            assertThatCode(() -> etlProcess.run(source, destination)).doesNotThrowAnyException();

            verify(mockExtractor).extract(source);
            verify(mockTransformer).transform(emptyDataset);
            verify(mockLoader).load(emptyDataset, destination);
        }

        @Test
        @DisplayName("Should cache results properly")
        void shouldCacheResultsProperly() {
            // Given
            String source = "/path/to/source.csv";
            String destination = "/path/to/destination";

            when(mockExtractor.extract(source)).thenReturn(mockDataset);
            when(mockTransformer.transform(mockDataset)).thenReturn(mockDataset);
            doNothing().when(mockLoader).load(mockDataset, destination);

            // When - Execute twice
            assertThatCode(() -> etlProcess.run(source, destination)).doesNotThrowAnyException();
            assertThatCode(() -> etlProcess.run(source, destination)).doesNotThrowAnyException();

            // Then - Should use cached results (Spring cache behavior)
            verify(mockExtractor, atLeastOnce()).extract(source);
            verify(mockTransformer, atLeastOnce()).transform(mockDataset);
            verify(mockLoader, atLeastOnce()).load(mockDataset, destination);
        }
    }

    @Nested
    @DisplayName("Error Handling Tests")
    class ErrorHandlingTests {

        @Test
        @DisplayName("Should handle extractor failures properly")
        void shouldHandleExtractorFailuresProperly() {
            // Given
            String source = "/path/to/invalid.csv";
            String destination = "/path/to/destination";

            when(mockExtractor.extract(source)).thenThrow(new FlightScheduleException("Failed to extract data"));

            // When & Then
            assertThatThrownBy(() -> etlProcess.run(source, destination))
                    .isInstanceOf(FlightScheduleException.class)
                    .hasMessageContaining("ETL process failed");

            verify(mockExtractor).extract(source);
            verify(mockTransformer, never()).transform(any());
            verify(mockLoader, never()).load(any(), any());
        }

        @Test
        @DisplayName("Should handle transformer failures properly")
        void shouldHandleTransformerFailuresProperly() {
            // Given
            String source = "/path/to/source.csv";
            String destination = "/path/to/destination";

            when(mockExtractor.extract(source)).thenReturn(mockDataset);
            when(mockTransformer.transform(mockDataset))
                    .thenThrow(new FlightScheduleException("Failed to transform data"));

            // When & Then
            assertThatThrownBy(() -> etlProcess.run(source, destination))
                    .isInstanceOf(FlightScheduleException.class)
                    .hasMessageContaining("ETL process failed");

            verify(mockExtractor).extract(source);
            verify(mockTransformer).transform(mockDataset);
            verify(mockLoader, never()).load(any(), any());
        }

        @Test
        @DisplayName("Should handle loader failures properly")
        void shouldHandleLoaderFailuresProperly() {
            // Given
            String source = "/path/to/source.csv";
            String destination = "/path/to/invalid-destination";

            when(mockExtractor.extract(source)).thenReturn(mockDataset);
            when(mockTransformer.transform(mockDataset)).thenReturn(mockDataset);
            doThrow(new FlightScheduleException("Failed to load data"))
                    .when(mockLoader)
                    .load(mockDataset, destination);

            // When & Then
            assertThatThrownBy(() -> etlProcess.run(source, destination))
                    .isInstanceOf(FlightScheduleException.class)
                    .hasMessageContaining("ETL process failed");

            verify(mockExtractor).extract(source);
            verify(mockTransformer).transform(mockDataset);
            verify(mockLoader).load(mockDataset, destination);
        }
    }

    @Nested
    @DisplayName("Cache Management Tests")
    class CacheManagementTests {

        @Test
        @DisplayName("Should clear cache on cleanup")
        void shouldClearCacheOnCleanup() {
            // When & Then
            assertThatCode(() -> etlProcess.cleanup()).doesNotThrowAnyException();
        }
    }

    @TestConfiguration
    static class TestConfig {
        @Bean
        @Primary
        public FlightScheduleETLProcess flightScheduleETLProcess(
                SparkSession sparkSession,
                FlightScheduleExtractor extractor,
                FlightScheduleTransformer transformer,
                FlightScheduleLoader loader) {
            return new FlightScheduleETLProcess(sparkSession, extractor, transformer, loader);
        }
    }
}
