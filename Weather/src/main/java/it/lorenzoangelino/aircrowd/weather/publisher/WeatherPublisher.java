package it.lorenzoangelino.aircrowd.weather.publisher;

import it.lorenzoangelino.aircrowd.common.models.locations.GeographicalLocation;
import it.lorenzoangelino.aircrowd.common.models.weather.WeatherDataForecast;

public interface WeatherPublisher {

    void start(GeographicalLocation location);

    void stop();

    void shutdown();

    boolean isRunning();

    void publishWeatherDataForecast(WeatherDataForecast data);
}
