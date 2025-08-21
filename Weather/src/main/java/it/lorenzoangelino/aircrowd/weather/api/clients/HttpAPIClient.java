package it.lorenzoangelino.aircrowd.weather.api.clients;

import it.lorenzoangelino.aircrowd.weather.api.params.QueryParam;
import java.io.IOException;
import java.util.List;
import lombok.Getter;
import lombok.Setter;
import org.apache.hc.client5.http.impl.async.CloseableHttpAsyncClient;
import org.apache.hc.client5.http.impl.async.HttpAsyncClients;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.Nullable;

@Getter
public class HttpAPIClient implements APIClient {
    protected final Logger logger;
    protected final CloseableHttpAsyncClient httpClient;

    @Setter
    @Nullable
    protected String baseURL;

    @Setter
    @Nullable
    protected List<QueryParam> baseQueryParams;

    public HttpAPIClient() {
        this.logger = LogManager.getLogger(HttpAPIClient.class);
        this.httpClient = createHttpClient();
        Runtime.getRuntime().addShutdownHook(new Thread(this::close));
    }

    @Override
    public void start() {
        this.logger.info("Start HTTP API client.");
        if (httpClient != null) httpClient.start();
        this.logger.info("HTTP API client has been started.");
    }

    @Override
    public void close() {
        this.logger.info("Closing HTTP API client.");
        if (httpClient != null)
            try {
                httpClient.close();
            } catch (IOException e) {
                this.logger.error("Error closing HTTP API client.", e);
            }
        this.logger.info("HTTP API client has been closed.");
    }

    private CloseableHttpAsyncClient createHttpClient() {
        return HttpAsyncClients.createDefault();
    }
}
