package it.lorenzoangelino.aircrowd.weather.retry;

import java.time.Duration;
import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class RetryPolicy {
    private static final Logger LOGGER = LogManager.getLogger(RetryPolicy.class);

    private final int maxAttempts;
    private final Duration initialDelay;
    private final double backoffMultiplier;

    public RetryPolicy(int maxAttempts, Duration initialDelay, double backoffMultiplier) {
        this.maxAttempts = maxAttempts;
        this.initialDelay = initialDelay;
        this.backoffMultiplier = backoffMultiplier;
    }

    public static RetryPolicy defaultPolicy() {
        return new RetryPolicy(3, Duration.ofSeconds(1), 2.0);
    }

    public static RetryPolicy exponentialBackoff(int maxAttempts) {
        return new RetryPolicy(maxAttempts, Duration.ofMillis(500), 2.0);
    }

    public <T> CompletableFuture<T> execute(Supplier<CompletableFuture<T>> operation, String operationName) {
        return executeWithRetry(operation, operationName, 1, initialDelay);
    }

    private <T> CompletableFuture<T> executeWithRetry(
            Supplier<CompletableFuture<T>> operation, String operationName, int attempt, Duration delay) {

        return operation
                .get()
                .handle((result, throwable) -> {
                    if (throwable == null) {
                        if (attempt > 1) {
                            LOGGER.info("Operation '{}' succeeded on attempt {}", operationName, attempt);
                        }
                        return CompletableFuture.completedFuture(result);
                    }

                    if (attempt >= maxAttempts) {
                        LOGGER.error("Operation '{}' failed after {} attempts", operationName, maxAttempts, throwable);
                        CompletableFuture<T> failedFuture = new CompletableFuture<>();
                        failedFuture.completeExceptionally(throwable);
                        return failedFuture;
                    }

                    LOGGER.warn(
                            "Operation '{}' failed on attempt {}, retrying in {}ms",
                            operationName,
                            attempt,
                            delay.toMillis(),
                            throwable);

                    Duration nextDelay = Duration.ofMillis((long) (delay.toMillis() * backoffMultiplier));

                    return CompletableFuture.runAsync(
                                    () -> {},
                                    CompletableFuture.delayedExecutor(
                                            delay.toMillis(), java.util.concurrent.TimeUnit.MILLISECONDS))
                            .thenCompose(v -> executeWithRetry(operation, operationName, attempt + 1, nextDelay));
                })
                .thenCompose(future -> future);
    }
}
