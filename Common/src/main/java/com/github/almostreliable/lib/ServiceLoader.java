package com.github.almostreliable.lib;

public class ServiceLoader {
    public static <T> T load(Class<T> clazz) {
        final T loadedService = java.util.ServiceLoader.load(clazz)
                .findFirst()
                .orElseThrow(() -> new NullPointerException("Failed to load service for " + clazz.getName()));
        AlmostLib.LOG.debug("Loaded {} for service {}", loadedService, clazz);
        return loadedService;
    }
}
