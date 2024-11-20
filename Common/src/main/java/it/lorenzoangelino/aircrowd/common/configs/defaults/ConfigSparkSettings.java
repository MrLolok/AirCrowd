package it.lorenzoangelino.aircrowd.common.configs.defaults;

import java.util.Map;

public record ConfigSparkSettings(
        String appName,
        String master,
        Map<String, String> config
) {}
