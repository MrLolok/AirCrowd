package it.lorenzoangelino.aircrowd.flightschedule.configs;

import java.util.Map;

public record ConfigSparkSettings(
        String appName,
        String master,
        Map<String, String> config
) {}
