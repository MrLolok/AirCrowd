package it.lorenzoangelino.aircrowd.weather.entities;

public record WeatherLocation(String name, double latitude, double longitude) implements IdentifiableEntity<String> {
    @Override
    public String getId() {
        return this.name;
    }
}
