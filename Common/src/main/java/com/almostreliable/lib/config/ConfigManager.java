package com.almostreliable.lib.config;

import com.almostreliable.lib.util.AlmostUtils;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

public class ConfigManager {

    private final static Map<Class<?>, Config<?>> CONFIGS = new HashMap<>();
    private final static List<Config<?>> CLIENT_RELOADABLES = new ArrayList<>();
    private final static List<Config<?>> SERVER_RELOADABLES = new ArrayList<>();

    public static <T> Config<T> get(Class<T> type) {
        var config = CONFIGS.get(type);
        if (config == null) {
            throw new IllegalArgumentException("No config registered for type " + type);
        }

        config.get(); // initialize the config so it's written
        return AlmostUtils.cast(config);
    }

    public static <T> void registerServerReloadable(Path path, Class<T> type, Function<ConfigBuilder, T> factory) {
        register(path, type, factory);
        SERVER_RELOADABLES.add(CONFIGS.get(type));
    }

    public static <T> void registerClientReloadable(Path path, Class<T> type, Function<ConfigBuilder, T> factory) {
        register(path, type, factory);
        CLIENT_RELOADABLES.add(CONFIGS.get(type));
    }

    public static <T> void register(Path path, Class<T> type, Function<ConfigBuilder, T> factory) {
        if (CONFIGS.containsKey(type)) {
            throw new IllegalArgumentException("Config already registered for type " + type);
        }

        Config<T> e = new Config<>(path.toFile(), type, factory);
        CONFIGS.put(type, e);
    }

    public static void reloadClient() {
        CLIENT_RELOADABLES.forEach(Config::reload);
    }

    public static void reloadServer() {
        SERVER_RELOADABLES.forEach(Config::reload);
    }
}
