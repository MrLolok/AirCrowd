package it.lorenzoangelino.aircrowd.weather.api.clients;

import it.lorenzoangelino.aircrowd.weather.api.params.QueryParam;
import lombok.Getter;
import lombok.Setter;
import org.apache.hc.client5.http.impl.async.CloseableHttpAsyncClient;
import org.apache.hc.client5.http.impl.async.HttpAsyncClients;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.util.List;

@Getter
public class HttpAPIClient implements APIClient {
    protected final CloseableHttpAsyncClient httpClient;
    protected final Logger logger;

    @Setter @Nullable
    protected String baseURL;
    @Setter @Nullable
    protected List<QueryParam> baseQueryParams;

    public HttpAPIClient() {
        this.httpClient = createHttpClient();
        this.logger = LogManager.getLogger("APIClient");
    }

    @Override
    public void close() throws IOException {
        this.logger.info("Closing HTTP API client.");
        if (httpClient != null)
            httpClient.close();
        this.logger.info("HTTP API client has been closed.");
    }

    private CloseableHttpAsyncClient createHttpClient() {
        return HttpAsyncClients.createDefault();
    }
}
