package it.lorenzoangelino.aircrowd.weather.api.clients;

import it.lorenzoangelino.aircrowd.weather.api.params.QueryParam;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.util.List;

public interface APIClient {
    @Nullable String getBaseURL();

    @Nullable List<QueryParam> getBaseQueryParams();

    void setBaseURL(@NotNull String baseURL);

    void setBaseQueryParams(@NotNull List<QueryParam> queryParams);

    void close() throws IOException;
}
