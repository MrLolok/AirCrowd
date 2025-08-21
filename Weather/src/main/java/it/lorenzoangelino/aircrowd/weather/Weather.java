package it.lorenzoangelino.aircrowd.weather;

import it.lorenzoangelino.aircrowd.common.configs.ConfigProvider;
import it.lorenzoangelino.aircrowd.common.models.locations.GeographicalLocation;
import it.lorenzoangelino.aircrowd.weather.services.WeatherService;
import it.lorenzoangelino.aircrowd.weather.services.WeatherServiceImpl;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public final class Weather {
    private static final Logger LOGGER = LogManager.getLogger(Weather.class);

    public static void main(String[] args) {
        GeographicalLocation analyzedWeatherLocation =
                ConfigProvider.getInstance().loadConfig("location", GeographicalLocation.class);
        LOGGER.info("Geographical location loaded: {}", analyzedWeatherLocation.toString());

        WeatherService service = new WeatherServiceImpl();
        LOGGER.info("Weather service initialized.");
        service.startAutomaticPublishing(analyzedWeatherLocation);
        LOGGER.info("Automatic weather publishing started.");
    }

    private static void example(WeatherService service, GeographicalLocation location) {
        service.getCurrentWeather(location).whenComplete((weather, throwable) -> {
            if (throwable == null) System.out.println(weather);
        });
    }
}
