package it.lorenzoangelino.aircrowd.weather.api.callbacks;

import org.apache.hc.client5.http.async.methods.SimpleHttpResponse;
import org.apache.hc.core5.concurrent.FutureCallback;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@FunctionalInterface
public interface ResponseCallback extends FutureCallback<SimpleHttpResponse> {
    Logger log = LoggerFactory.getLogger(ResponseCallback.class);

    void onSuccess(SimpleHttpResponse response);

    default void onFailure(Exception e) {
        log.error("Request failed: {}", e.getMessage());
    }

    @Override
    default void completed(SimpleHttpResponse response) {
        log.info("Request completed with status: {}", response.getCode());
        onSuccess(response);
    }

    @Override
    default void failed(Exception e) {
        onFailure(e);
    }

    @Override
    default void cancelled() {
        log.info("Request cancelled.");
    }
}
