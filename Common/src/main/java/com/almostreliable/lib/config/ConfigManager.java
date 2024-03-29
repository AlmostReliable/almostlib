package com.almostreliable.lib.config;

import com.almostreliable.lib.AlmostLib;
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
 * Use {@link ConfigHolder#get()} to get the config and store it statically in the
 * initializer of your mod for an early initialization.
 * <p>
 * Example:
 * <pre>{@code
 * ConfigManager.registerServerReloadable("example.toml", ExampleConfig.class, ExampleConfig::new);
 * public static final Config<ExampleConfig> CONFIG = ConfigManager.get(ExampleConfig.class);
 * }</pre>
 */
public final class ConfigManager {

    private static final Map<Class<?>, ConfigHolder<?>> CONFIGS = new HashMap<>();
    private static final List<ConfigHolder<?>> CLIENT_RELOADABLES = new ArrayList<>();
    private static final List<ConfigHolder<?>> SERVER_RELOADABLES = new ArrayList<>();

    private ConfigManager() {}

    /**
     * Gets the config with the given class.
     *
     * @param clazz The class of the config.
     * @param <T>   The type of the config.
     * @return The config as type-safe object.
     */
    public static <T> ConfigHolder<T> get(Class<T> clazz) {
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
     * Use {@link ConfigHolder#get()} to get the config.
     *
     * @param path    The path to the config file.
     * @param clazz   The class of the config.
     * @param factory The factory that creates the config.
     * @param <T>     The type of the config.
     */
    public static <T> void registerServerReloadable(String path, Class<T> clazz, Function<ConfigBuilder, T> factory) {
        register(path, clazz, factory);
        SERVER_RELOADABLES.add(CONFIGS.get(clazz));
    }

    /**
     * Registers a config that is reloadable on the client.<br>
     * Reloads are handled automatically by the library.
     * <p>
     * After registering the config, it should be stored statically in the mod's main class
     * in order to be initialized and written to disk.<br>
     * Use {@link ConfigHolder#get()} to get the config.
     *
     * @param path    The path to the config file.
     * @param clazz   The class of the config.
     * @param factory The factory that creates the config.
     * @param <T>     The type of the config.
     */
    public static <T> void registerClientReloadable(String path, Class<T> clazz, Function<ConfigBuilder, T> factory) {
        register(path, clazz, factory);
        CLIENT_RELOADABLES.add(CONFIGS.get(clazz));
    }

    /**
     * Registers a config that is not reloadable.
     * <p>
     * After registering the config, it should be stored statically in the mod's main class
     * in order to be initialized and written to disk.<br>
     * Use {@link ConfigHolder#get()} to get the config.
     *
     * @param path    The path to the config file.
     * @param clazz   The class of the config.
     * @param factory The factory that creates the config.
     * @param <T>     The type of the config.
     */
    public static <T> void register(String path, Class<T> clazz, Function<ConfigBuilder, T> factory) {
        if (CONFIGS.containsKey(clazz)) {
            throw new IllegalArgumentException("Config already registered for class " + clazz);
        }

        Path filePath = AlmostLib.PLATFORM.getConfigPath().resolve(path);
        ConfigHolder<T> e = new ConfigHolder<>(filePath, clazz, factory);
        CONFIGS.put(clazz, e);
    }

    @ApiStatus.Internal
    public static final class ReloadHandler {

        private ReloadHandler() {}

        public static void reloadClient() {
            CLIENT_RELOADABLES.forEach(ConfigHolder::reload);
        }

        public static void reloadServer() {
            SERVER_RELOADABLES.forEach(ConfigHolder::reload);
        }
    }
}
