package it.lorenzoangelino.aircrowd.weather.api.params;

import org.jetbrains.annotations.NotNull;

public record QueryParam(@NotNull String key, @NotNull String value) {
    public static QueryParam of(@NotNull String key, @NotNull String value) {
        return new QueryParam(key, value);
    }
}
