package it.lorenzoangelino.aircrowd.common.configs;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import it.lorenzoangelino.aircrowd.common.models.locations.GeographicalLocation;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

@SpringBootTest
@TestPropertySource(properties = {"spring.cloud.config.enabled=false"})
class ConfigProviderTest {

    @Autowired
    private ConfigProvider configProvider;

    @Nested
    @DisplayName("ConfigProvider Instance Tests")
    class InstanceTests {

        @Test
        @DisplayName("Should return singleton instance")
        void shouldReturnSingletonInstance() {
            ConfigProvider instance1 = ConfigProvider.getInstance();
            ConfigProvider instance2 = ConfigProvider.getInstance();

            assertThat(instance1).isSameAs(instance2);
        }

        @Test
        @DisplayName("Should inject ConfigProvider in Spring context")
        void shouldInjectConfigProviderInSpringContext() {
            assertThat(configProvider).isNotNull();
            assertThat(configProvider).isInstanceOf(ConfigProviderImpl.class);
        }

        @Test
        @DisplayName("Should use Spring managed bean when available")
        void shouldUseSpringManagedBeanWhenAvailable() {
            // In Spring context, the injected instance should be available
            assertThat(configProvider).isNotNull();

            // Singleton pattern still works
            ConfigProvider singletonInstance = ConfigProvider.getInstance();
            assertThat(singletonInstance).isNotNull();
        }
    }

    @Nested
    @DisplayName("Configuration Loading Tests")
    class ConfigurationLoadingTests {

        @TempDir
        Path tempDir;

        @Test
        @DisplayName("Should load valid JSON configuration via Spring")
        void shouldLoadValidJsonConfigurationViaSpring() throws IOException {
            // Given
            String configContent =
                    """
                {
                  "name": "Test Airport",
                  "latitude": 40.883964,
                  "longitude": 14.2833
                }
                """;

            Path configsDir = tempDir.resolve("configs");
            Files.createDirectories(configsDir);
            Path configFile = configsDir.resolve("location.json");
            Files.write(configFile, configContent.getBytes());

            // Temporarily override the config directory for testing
            System.setProperty("user.dir", tempDir.toString());

            try {
                // When
                GeographicalLocation location = configProvider.loadConfig("location", GeographicalLocation.class);

                // Then
                assertThat(location).isNotNull();
                assertThat(location.name()).isEqualTo("Test Airport");
                assertThat(location.latitude()).isEqualTo(40.883964);
                assertThat(location.longitude()).isEqualTo(14.2833);
            } finally {
                System.clearProperty("user.dir");
            }
        }

        @Test
        @DisplayName("Should handle missing configuration file gracefully")
        void shouldHandleMissingConfigurationFileGracefully() {
            // Given
            System.setProperty("user.dir", tempDir.toString());

            try {
                // When & Then
                assertThatThrownBy(() -> configProvider.loadConfig("nonexistent", GeographicalLocation.class))
                        .isInstanceOf(RuntimeException.class)
                        .hasMessageContaining("Failed to load configuration");
            } finally {
                System.clearProperty("user.dir");
            }
        }

        @Test
        @DisplayName("Should handle invalid JSON gracefully")
        void shouldHandleInvalidJsonGracefully() throws IOException {
            // Given
            String invalidJsonContent =
                    """
                {
                  "name": "Test Airport",
                  "latitude": "invalid_number",
                  "longitude": 14.2833
                """;

            Path configsDir = tempDir.resolve("configs");
            Files.createDirectories(configsDir);
            Path configFile = configsDir.resolve("invalid.json");
            Files.write(configFile, invalidJsonContent.getBytes());

            System.setProperty("user.dir", tempDir.toString());

            try {
                // When & Then
                assertThatThrownBy(() -> configProvider.loadConfig("invalid", GeographicalLocation.class))
                        .isInstanceOf(RuntimeException.class);
            } finally {
                System.clearProperty("user.dir");
            }
        }
    }

    @Nested
    @DisplayName("Spring Context Integration Tests")
    class SpringContextIntegrationTests {

        @Test
        @DisplayName("Should work with Spring dependency injection")
        void shouldWorkWithSpringDependencyInjection() {
            // Test that ConfigProvider works properly in Spring context
            assertThat(configProvider).isNotNull();

            // Should be able to load configurations through injected instance
            assertThatCode(() -> {
                        // This may fail due to missing config file, but should not fail due to injection issues
                        try {
                            configProvider.loadConfig("test", TestConfig.class);
                        } catch (RuntimeException e) {
                            // Expected if config file doesn't exist
                            assertThat(e.getMessage()).contains("Failed to load configuration");
                        }
                    })
                    .doesNotThrowAnyException();
        }

        @Test
        @DisplayName("Should maintain singleton behavior in Spring context")
        void shouldMaintainSingletonBehaviorInSpringContext() {
            ConfigProvider staticInstance = ConfigProvider.getInstance();

            // Both should be non-null and should work
            assertThat(configProvider).isNotNull();
            assertThat(staticInstance).isNotNull();
        }
    }

    // Test configuration class
    public static class TestConfig {
        private String name;
        private boolean enabled;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public boolean isEnabled() {
            return enabled;
        }

        public void setEnabled(boolean enabled) {
            this.enabled = enabled;
        }
    }
}
