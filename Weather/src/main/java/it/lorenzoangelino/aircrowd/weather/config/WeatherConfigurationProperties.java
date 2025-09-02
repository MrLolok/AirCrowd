package it.lorenzoangelino.aircrowd.weather.config;

import java.util.concurrent.TimeUnit;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "weather")
public record WeatherConfigurationProperties(
        OpenMeteo openmeteo, Publisher publisher, Responses responses, Location location) {

    public WeatherConfigurationProperties {
        if (openmeteo == null) {
            openmeteo = new OpenMeteo(
                    "https://api.open-meteo.com/v1",
                    7,
                    "temperature_2m,relative_humidity_2m,dew_point_2m,precipitation_probability,rain,showers,snowfall,pressure_msl,cloud_cover,visibility,wind_speed_10m,wind_direction_10m",
                    "Europe/Rome");
        }
        if (publisher == null) {
            publisher = new Publisher("weather-data", new Publisher.Task(0, 15, TimeUnit.MINUTES));
        }
        if (responses == null) {
            responses = new Responses(false, "weather_data");
        }
        if (location == null) {
            location = new Location("Default Airport Location", 45.6269, 8.7203);
        }
    }

    public record OpenMeteo(String baseUrl, int forecastDays, String hourly, String timezone) {
        public OpenMeteo {
            if (baseUrl == null || baseUrl.isBlank()) {
                baseUrl = "https://api.open-meteo.com/v1";
            }
            if (forecastDays <= 0) {
                forecastDays = 7;
            }
            if (hourly == null || hourly.isBlank()) {
                hourly =
                        "temperature_2m,relative_humidity_2m,dew_point_2m,precipitation_probability,rain,showers,snowfall,pressure_msl,cloud_cover,visibility,wind_speed_10m,wind_direction_10m";
            }
            if (timezone == null || timezone.isBlank()) {
                timezone = "Europe/Rome";
            }
        }
    }

    public record Publisher(String weatherDataOutputTopic, Task task) {
        public Publisher {
            if (weatherDataOutputTopic == null || weatherDataOutputTopic.isBlank()) {
                weatherDataOutputTopic = "weather-data";
            }
            if (task == null) {
                task = new Task(0, 15, TimeUnit.MINUTES);
            }
        }

        public record Task(long delay, long period, TimeUnit unit) {
            public Task {
                if (delay < 0) delay = 0;
                if (period <= 0) period = 15;
                if (unit == null) unit = TimeUnit.MINUTES;
            }
        }
    }

    public record Responses(boolean save, String table) {
        public Responses {
            if (table == null || table.isBlank()) {
                table = "weather_data";
            }
        }
    }

    public record Location(String name, double latitude, double longitude) {
        public Location {
            if (name == null || name.isBlank()) {
                name = "Default Airport Location";
            }
        }
    }
}
