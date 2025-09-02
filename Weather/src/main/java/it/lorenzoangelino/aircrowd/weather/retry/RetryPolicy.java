package it.lorenzoangelino.aircrowd.weather.retry;

import java.time.Duration;
import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class RetryPolicy {

    private final int maxAttempts;
    private final Duration initialDelay;

    public RetryPolicy() {
        this.maxAttempts = 3;
        this.initialDelay = Duration.ofSeconds(1);
    }

    public RetryPolicy(int maxAttempts, Duration initialDelay) {
        this.maxAttempts = maxAttempts;
        this.initialDelay = initialDelay;
    }

    public <T> CompletableFuture<T> execute(Supplier<CompletableFuture<T>> operation, String operationName) {
        return executeWithRetry(operation, operationName, 1);
    }

    private <T> CompletableFuture<T> executeWithRetry(
            Supplier<CompletableFuture<T>> operation, String operationName, int attempt) {

        return operation
                .get()
                .handle((result, throwable) -> {
                    if (throwable == null) {
                        if (attempt > 1) {
                            log.info("Operation '{}' succeeded on attempt {}", operationName, attempt);
                        }
                        return CompletableFuture.completedFuture(result);
                    }

                    if (attempt >= maxAttempts) {
                        log.error("Operation '{}' failed after {} attempts", operationName, maxAttempts, throwable);
                        CompletableFuture<T> failedFuture = new CompletableFuture<>();
                        failedFuture.completeExceptionally(throwable);
                        return failedFuture;
                    }

                    long delayMs = initialDelay.toMillis() * attempt;
                    log.warn(
                            "Operation '{}' failed on attempt {}, retrying in {}ms",
                            operationName,
                            attempt,
                            delayMs,
                            throwable);

                    return CompletableFuture.runAsync(
                                    () -> {},
                                    CompletableFuture.delayedExecutor(
                                            delayMs, java.util.concurrent.TimeUnit.MILLISECONDS))
                            .thenCompose(v -> executeWithRetry(operation, operationName, attempt + 1));
                })
                .thenCompose(future -> future);
    }
}
