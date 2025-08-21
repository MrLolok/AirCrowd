package it.lorenzoangelino.aircrowd.common.kafka.producer;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import it.lorenzoangelino.aircrowd.common.models.IdentifiableModel;
import java.util.concurrent.Future;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.test.context.TestPropertySource;
import org.springframework.util.concurrent.ListenableFuture;

@SpringBootTest
@EmbeddedKafka(
        partitions = 1,
        topics = {"test-topic"})
@TestPropertySource(
        properties = {
            "spring.kafka.bootstrap-servers=${spring.embedded.kafka.brokers}",
            "spring.cloud.config.enabled=false"
        })
class KafkaProducerServiceTest {

    @Autowired
    private KafkaProducerService kafkaProducerService;

    @MockBean
    private KafkaTemplate<String, String> kafkaTemplate;

    @Nested
    @DisplayName("Message Sending Tests")
    class MessageSendingTests {

        @Test
        @DisplayName("Should send message successfully using Spring Kafka")
        void shouldSendMessageSuccessfullyWithSpringKafka() throws Exception {
            // Given
            String topic = "test-topic";
            String key = "test-key";
            String value = "test-value";

            RecordMetadata mockMetadata = mock(RecordMetadata.class);
            when(mockMetadata.topic()).thenReturn(topic);
            when(mockMetadata.partition()).thenReturn(0);
            when(mockMetadata.offset()).thenReturn(1L);

            SendResult<String, String> mockResult = mock(SendResult.class);
            when(mockResult.getRecordMetadata()).thenReturn(mockMetadata);

            ListenableFuture<SendResult<String, String>> mockFuture = mock(ListenableFuture.class);
            when(kafkaTemplate.send(topic, key, value)).thenReturn(mockFuture);

            // When
            Future<RecordMetadata> future = kafkaProducerService.send(topic, key, value);

            // Then
            assertThat(future).isNotNull();
            verify(kafkaTemplate).send(topic, key, value);
        }

        @Test
        @DisplayName("Should send entity message successfully")
        void shouldSendEntityMessageSuccessfully() throws Exception {
            // Given
            String topic = "test-topic";
            TestEntity entity = new TestEntity("test-id", "test-data");

            String expectedJson = "{\"id\":\"test-id\",\"data\":\"test-data\"}";

            ListenableFuture<SendResult<String, String>> mockFuture = mock(ListenableFuture.class);
            when(kafkaTemplate.send(eq(topic), eq(entity.getId()), anyString())).thenReturn(mockFuture);

            // When
            Future<RecordMetadata> future = kafkaProducerService.send(topic, entity);

            // Then
            assertThat(future).isNotNull();
            verify(kafkaTemplate).send(eq(topic), eq(entity.getId()), anyString());
        }

        @Test
        @DisplayName("Should handle null key gracefully")
        void shouldHandleNullKeyGracefully() {
            // Given
            String topic = "test-topic";
            String key = null;
            String value = "test-value";

            ListenableFuture<SendResult<String, String>> mockFuture = mock(ListenableFuture.class);
            when(kafkaTemplate.send(topic, key, value)).thenReturn(mockFuture);

            // When
            Future<RecordMetadata> future = kafkaProducerService.send(topic, key, value);

            // Then
            assertThat(future).isNotNull();
            verify(kafkaTemplate).send(topic, key, value);
        }
    }

    @Nested
    @DisplayName("Service Lifecycle Tests")
    class ServiceLifecycleTests {

        @Test
        @DisplayName("Should close service gracefully")
        void shouldCloseServiceGracefully() {
            // When & Then - Should not throw any exception
            assertThatCode(() -> kafkaProducerService.close()).doesNotThrowAnyException();
        }
    }

    // Test entity for testing
    private static class TestEntity implements IdentifiableModel<String> {
        private final String id;
        private final String data;

        public TestEntity(String id, String data) {
            this.id = id;
            this.data = data;
        }

        @Override
        public String getId() {
            return id;
        }

        public String getData() {
            return data;
        }
    }
}
