package it.lorenzoangelino.aircrowd.weather.api.uri;

import java.net.URI;
import java.net.URISyntaxException;

public interface RequestURI {
    URI getURI() throws URISyntaxException;
}
