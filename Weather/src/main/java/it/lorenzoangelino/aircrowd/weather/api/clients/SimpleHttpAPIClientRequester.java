package it.lorenzoangelino.aircrowd.weather.api.clients;

import it.lorenzoangelino.aircrowd.weather.api.callbacks.ResponseCallback;
import it.lorenzoangelino.aircrowd.weather.api.params.QueryParam;
import it.lorenzoangelino.aircrowd.weather.api.uri.RequestURI;
import it.lorenzoangelino.aircrowd.weather.api.uri.ParameterizedRequestURI;
import org.apache.hc.client5.http.async.methods.SimpleHttpRequest;
import org.apache.hc.core5.http.Method;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SimpleHttpAPIClientRequester extends HttpAPIClient implements APIClientRequester {
    @Override
    public void request(@Nullable ResponseCallback callback, @NotNull Method method, @Nullable QueryParam... params) {
        request(callback, method, null, params);
    }

    @Override
    public void request(@Nullable ResponseCallback callback, @NotNull Method method, @Nullable String path, @Nullable QueryParam... params) {
        SimpleHttpRequest request = this.createRequest(method, path, params);
        this.httpClient.execute(request, callback);
    }

    @Override
    public void get(@Nullable ResponseCallback callback, @Nullable QueryParam... params) {
        get(callback, null, params);
    }

    @Override
    public void get(@Nullable ResponseCallback callback, @Nullable String path, @Nullable QueryParam... params) {
        this.request(callback, Method.GET, path, params);
    }

    @Override
    public void post(@Nullable ResponseCallback callback, @Nullable QueryParam... params) {
        post(callback, null, params);
    }

    @Override
    public void post(@Nullable ResponseCallback callback, @Nullable String path, @Nullable QueryParam... params) {
        this.request(callback, Method.GET, path, params);
    }

    @Override
    public void put(@Nullable ResponseCallback callback, @Nullable QueryParam... params) {
        put(callback, null, params);
    }

    @Override
    public void put(@Nullable ResponseCallback callback, @Nullable String path, @Nullable QueryParam... params) {
        this.request(callback, Method.GET, path, params);
    }

    @Override
    public void delete(@Nullable ResponseCallback callback, @Nullable QueryParam... params) {
        delete(callback, null, params);
    }

    @Override
    public void delete(@Nullable ResponseCallback callback, @Nullable String path, @Nullable QueryParam... params) {
        this.request(callback, Method.DELETE, path, params);
    }

    protected SimpleHttpRequest createRequest(@NotNull Method method, String path, QueryParam... params) {
        if (this.baseURL == null)
            throw new RuntimeException("Base URL not set.");

        List<QueryParam> queryParams = new ArrayList<>();
        if (this.baseQueryParams != null)
            queryParams.addAll(this.baseQueryParams);
        if (params != null)
            queryParams.addAll(Arrays.stream(params).toList());

        RequestURI requestURI = new ParameterizedRequestURI(this.baseURL, path, queryParams);
        try {
            URI uri = requestURI.getURI();
            return SimpleHttpRequest.create(method, uri);
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }
}
