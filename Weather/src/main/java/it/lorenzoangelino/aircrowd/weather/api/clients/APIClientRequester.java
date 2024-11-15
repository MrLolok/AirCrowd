package it.lorenzoangelino.aircrowd.weather.api.clients;

import it.lorenzoangelino.aircrowd.weather.api.callbacks.ResponseCallback;
import it.lorenzoangelino.aircrowd.weather.api.params.QueryParam;
import org.apache.hc.core5.http.Method;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface APIClientRequester extends APIClient {
    void request(@Nullable ResponseCallback callback, @NotNull Method method, @Nullable QueryParam... params);

    void request(@Nullable ResponseCallback callback, @NotNull Method method, @Nullable String path, @Nullable QueryParam... params);

    void get(@Nullable ResponseCallback callback, @Nullable QueryParam... params);

    void get(@Nullable ResponseCallback callback, @Nullable String path, @Nullable QueryParam... params);

    void post(@Nullable ResponseCallback callback, @Nullable QueryParam... params);

    void post(@Nullable ResponseCallback callback, @Nullable String path, @Nullable QueryParam... params);

    void put(@Nullable ResponseCallback callback, @Nullable QueryParam... params);

    void put(@Nullable ResponseCallback callback, @Nullable String path, @Nullable QueryParam... params);

    void delete(@Nullable ResponseCallback callback, @Nullable QueryParam... params);

    void delete(@Nullable ResponseCallback callback, @Nullable String path, @Nullable QueryParam... params);
}
