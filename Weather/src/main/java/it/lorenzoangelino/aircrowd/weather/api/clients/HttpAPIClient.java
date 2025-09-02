package it.lorenzoangelino.aircrowd.weather.api.clients;

import it.lorenzoangelino.aircrowd.weather.api.params.QueryParam;
import java.io.IOException;
import java.util.List;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.hc.client5.http.impl.async.CloseableHttpAsyncClient;
import org.apache.hc.client5.http.impl.async.HttpAsyncClients;
import org.jetbrains.annotations.Nullable;

@Getter
@Slf4j
public class HttpAPIClient implements APIClient {
    protected final CloseableHttpAsyncClient httpClient;

    @Setter
    @Nullable
    protected String baseURL;

    @Setter
    @Nullable
    protected List<QueryParam> baseQueryParams;

    public HttpAPIClient() {
        this.httpClient = createHttpClient();
        Runtime.getRuntime().addShutdownHook(new Thread(this::close));
    }

    @Override
    public void start() {
        log.info("Start HTTP API client.");
        if (httpClient != null) httpClient.start();
        log.info("HTTP API client has been started.");
    }

    @Override
    public void close() {
        log.info("Closing HTTP API client.");
        if (httpClient != null)
            try {
                httpClient.close();
            } catch (IOException e) {
                log.error("Error closing HTTP API client.", e);
            }
        log.info("HTTP API client has been closed.");
    }

    private CloseableHttpAsyncClient createHttpClient() {
        return HttpAsyncClients.createDefault();
    }
}
