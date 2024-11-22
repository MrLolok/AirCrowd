package it.lorenzoangelino.aircrowd.weather;

import it.lorenzoangelino.aircrowd.common.configs.ConfigProvider;
import it.lorenzoangelino.aircrowd.common.models.locations.GeographicalLocation;
import it.lorenzoangelino.aircrowd.weather.services.WeatherService;
import it.lorenzoangelino.aircrowd.weather.services.WeatherServiceImpl;

public final class Weather {
    public static void main(String[] args) {
        GeographicalLocation analyzedWeatherLocation = ConfigProvider.getInstance().loadConfig("location", GeographicalLocation.class);
        WeatherService service = new WeatherServiceImpl();
        service.startAutomaticPublishing(analyzedWeatherLocation);
        service.getCurrentWeather(analyzedWeatherLocation).whenComplete((weather, throwable) -> {
            if (throwable == null)
                System.out.println(weather);
        });
    }
}
