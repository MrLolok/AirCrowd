package it.lorenzoangelino.aircrowd.weather.api.uri;

import it.lorenzoangelino.aircrowd.weather.api.params.QueryParam;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.apache.hc.core5.net.URIBuilder;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@AllArgsConstructor
@Getter
@Setter
public class ParameterizedRequestURI implements RequestURI {
    private @NotNull String baseURL;
    private @Nullable String path;
    private @Nullable List<QueryParam> params;

    public ParameterizedRequestURI(@NotNull String baseURL) {
        this.baseURL = baseURL;
    }

    @Override
    public URI getURI() throws URISyntaxException {
        String url = this.baseURL;
        if (path != null) url += path;

        URIBuilder builder = new URIBuilder(url);
        if (params != null)
            for (QueryParam param : params) if (param != null) builder.addParameter(param.key(), param.value());
        return builder.build();
    }
}
