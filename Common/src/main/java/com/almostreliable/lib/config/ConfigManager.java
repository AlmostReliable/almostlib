package com.almostreliable.lib.config;

import com.almostreliable.lib.util.AlmostUtils;
import org.jetbrains.annotations.ApiStatus;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

/**
 * The central point of the config library.<br>
 * Holds all registered configs and handles reloads.
 * <p>
 * The path is relative to the config folder of the game. Use the path
 * to specify whether the config should be placed inside a sub folder or
 * not.<br>
 * The folder structure will be automatically created if it doesn't exist.<br>
 * All files must be HOCON files.
 * <p>
 * After registering a config, it's lazily initialized and written to disk
 * upon first access.<br>
 * Use {@link Config#get()} to get the config and store it statically in the
 * initializer of your mod for an early initialization.
 */
public final class ConfigManager {

    private static final Map<Class<?>, Config<?>> CONFIGS = new HashMap<>();
    private static final List<Config<?>> CLIENT_RELOADABLES = new ArrayList<>();
    private static final List<Config<?>> SERVER_RELOADABLES = new ArrayList<>();

    private ConfigManager() {}

    /**
     * Gets the config with the given class.
     *
     * @param clazz The class of the config.
     * @param <T>   The type of the config.
     * @return The config as type-safe object.
     */
    public static <T> Config<T> get(Class<T> clazz) {
        var config = CONFIGS.get(clazz);
        if (config == null) {
            throw new IllegalArgumentException("No config registered for class " + clazz);
        }

        config.get(); // initialize the config on first access
        return AlmostUtils.cast(config);
    }

    /**
     * Registers a config that is reloadable on the server.<br>
     * Reloads are handled automatically by the library.
     * <p>
     * After registering the config, it should be stored statically in the mod's main class
     * in order to be initialized and written to disk.<br>
     * Use {@link Config#get()} to get the config.
     *
     * @param path    The path to the config file.
     * @param clazz   The class of the config.
     * @param factory The factory that creates the config.
     * @param <T>     The type of the config.
     */
    public static <T> void registerServerReloadable(Path path, Class<T> clazz, Function<ConfigBuilder, T> factory) {
        register(path, clazz, factory);
        SERVER_RELOADABLES.add(CONFIGS.get(clazz));
    }

    /**
     * Registers a config that is reloadable on the client.<br>
     * Reloads are handled automatically by the library.
     * <p>
     * After registering the config, it should be stored statically in the mod's main class
     * in order to be initialized and written to disk.<br>
     * Use {@link Config#get()} to get the config.
     *
     * @param path    The path to the config file.
     * @param clazz   The class of the config.
     * @param factory The factory that creates the config.
     * @param <T>     The type of the config.
     */
    public static <T> void registerClientReloadable(Path path, Class<T> clazz, Function<ConfigBuilder, T> factory) {
        register(path, clazz, factory);
        CLIENT_RELOADABLES.add(CONFIGS.get(clazz));
    }

    /**
     * Registers a config that is not reloadable.
     * <p>
     * After registering the config, it should be stored statically in the mod's main class
     * in order to be initialized and written to disk.<br>
     * Use {@link Config#get()} to get the config.
     *
     * @param path    The path to the config file.
     * @param clazz   The class of the config.
     * @param factory The factory that creates the config.
     * @param <T>     The type of the config.
     */
    public static <T> void register(Path path, Class<T> clazz, Function<ConfigBuilder, T> factory) {
        if (CONFIGS.containsKey(clazz)) {
            throw new IllegalArgumentException("Config already registered for class " + clazz);
        }

        Config<T> e = new Config<>(path.toFile(), clazz, factory);
        CONFIGS.put(clazz, e);
    }

    @ApiStatus.Internal
    public static final class ReloadHandler {

        private ReloadHandler() {}

        public static void reloadClient() {
            CLIENT_RELOADABLES.forEach(Config::reload);
        }

        public static void reloadServer() {
            SERVER_RELOADABLES.forEach(Config::reload);
        }
    }
}
