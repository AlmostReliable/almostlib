package com.almostreliable.almostlib;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ServiceLoader;

public class AlmostLib {
    public static final Logger LOGGER = LogManager.getLogger(BuildConfig.MOD_NAME);
    public static final AlmostLibPlatform PLATFORM = loadService(AlmostLibPlatform.class);

    public static <T> T loadService(Class<T> clazz) {
        return ServiceLoader
                .load(clazz)
                .findFirst()
                .orElseThrow(() -> new NullPointerException("Failed to load service for class: " + clazz.getName()));
    }
}
