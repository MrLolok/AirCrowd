package it.lorenzoangelino.aircrowd.prediction.utils;

import lombok.experimental.UtilityClass;

@UtilityClass
public class DataNormalizer {
    public static double normalize(double value, double min, double max) {
        return Math.max(0, Math.min(1, (value - min) / (max - min)));
    }
}
