package com.github.almostreliable.lib.api;

import com.github.almostreliable.lib.AlmostConstants;

public class ServiceLoader {
    public static <T> T load(Class<T> clazz) {
        final T loadedService = java.util.ServiceLoader.load(clazz)
                .findFirst()
                .orElseThrow(() -> new NullPointerException("Failed to load service for " + clazz.getName()));
        AlmostConstants.LOG.debug("Loaded {} for service {}", loadedService, clazz);
        return loadedService;
    }
}
