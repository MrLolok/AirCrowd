package it.lorenzoangelino.aircrowd.weather.api.callbacks;

import org.apache.hc.client5.http.async.methods.SimpleHttpResponse;
import org.apache.hc.core5.concurrent.FutureCallback;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@FunctionalInterface
public interface ResponseCallback extends FutureCallback<SimpleHttpResponse> {
    Logger LOGGER = LogManager.getLogger(ResponseCallback.class);

    void onSuccess(SimpleHttpResponse response);

    @Override
    default void completed(SimpleHttpResponse response) {
        LOGGER.info("Request completed with status: {}", response.getCode());
        onSuccess(response);
    }

    @Override
    default void failed(Exception e) {
        LOGGER.error("Request failed: {}", e.getMessage());
    }

    @Override
    default void cancelled() {
        LOGGER.info("Request cancelled.");
    }
}
