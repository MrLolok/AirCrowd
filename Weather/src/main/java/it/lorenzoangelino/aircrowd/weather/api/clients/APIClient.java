package it.lorenzoangelino.aircrowd.weather.api.clients;

import it.lorenzoangelino.aircrowd.weather.api.params.QueryParam;
import java.io.IOException;
import java.util.List;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface APIClient {
    @Nullable
    String getBaseURL();

    @Nullable
    List<QueryParam> getBaseQueryParams();

    void setBaseURL(@NotNull String baseURL);

    void setBaseQueryParams(@NotNull List<QueryParam> queryParams);

    void start();

    void close() throws IOException;
}
