package it.lorenzoangelino.aircrowd.weather.configs;

import java.util.concurrent.TimeUnit;

public record ConfigPublisherSettings(long delay, long period, TimeUnit unit) {
}
