package it.lorenzoangelino.aircrowd.weather.config.defaults;

import java.util.concurrent.TimeUnit;

public record ConfigPublisherSettings(long delay, long period, TimeUnit unit) {
}
