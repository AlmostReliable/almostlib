package com.almostreliable.lib.config;

import com.almostreliable.lib.AlmostLib;
import com.almostreliable.lib.Utils;
import com.almostreliable.lib.utils.BooleanRef;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableSet;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.lang.reflect.Field;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.util.*;

/**
 * A class that manages the configuration of the mod.
 * <p>
 * This class is responsible for reading and writing the configuration to and from a file.
 * It also handles the creation of the configuration file if it doesn't exist.
 * <p>
 * To create a new file just use {@link #of(Class, String)} or {@link #ofRoot(Class, String)}.
 * Before using the config, it has to be loaded with {@link #load()}.
 * <p>
 * Possible field types are allowed:
 * <ul>
 *     <li>{@link String}</li>
 *     <li>Primitives like {@link Integer}, {@link Float}, {@link Double}, {@link Boolean}</li>
 *     <li>Collections like {@link List}, {@link Set} or arrays</li>
 *     <li>{@link Map}</li>
 *     <li>{@link Enum}</li>
 *     <li>Custom objects to also represent nested values</li>
 * </ul>
 *
 * @param <T>
 */
public class Config<T> {
    private static final List<String> ALLOWED_EXTENSIONS = List.of("json");

    private final Class<T> clazz;
    private final Path path;
    private final Set<Class<?>> collectedClasses;
    @Nullable private T value;

    private Config(Class<T> clazz, Path path) {
        this.clazz = clazz;
        this.path = path;
        Preconditions.checkArgument(ConfigHelper.isUsableTypeForConfig(clazz),
                "Class " + clazz.getSimpleName() + " is not represented as a JSON object");
        Preconditions.checkArgument(ALLOWED_EXTENSIONS.stream().anyMatch(ext -> path.toString().endsWith(ext)),
                "File extension must be one of " + ALLOWED_EXTENSIONS);

        Set<Class<?>> collectedClasses = new HashSet<>();
        collectedClasses.add(clazz);
        collectNeededClassTypes(clazz, collectedClasses);
        this.collectedClasses = ImmutableSet.<Class<?>>builder().addAll(collectedClasses).build();
    }

    /**
     * Creates new Config for given class and relative path to config directory.
     *
     * @param clazz class to create config for
     * @param path  relative path to config directory
     * @param <T>   type of class
     * @return new Config
     */
    public static <T> Config<T> of(Class<T> clazz, String path) {
        return new Config<>(clazz, AlmostLib.INSTANCE.getConfigPath().resolve(path));
    }

    /**
     * Creates new config for given class and relative path to game directory.
     *
     * @param clazz class to create config for
     * @param path  relative path to game directory
     * @param <T>   type of class
     * @return new Config
     */
    public static <T> Config<T> ofRoot(Class<T> clazz, String path) {
        return new Config<>(clazz, AlmostLib.INSTANCE.getRootPath().resolve(path));
    }

    /**
     * Requires that config is loaded.
     *
     * @return the config
     */
    @Nonnull
    public T get() {
        if (value == null) {
            throw new IllegalStateException("Config " + clazz.getSimpleName() + " has not been loaded");
        }

        return value;
    }

    public String getName() {
        return clazz.getSimpleName();
    }

    /**
     * Loads the config from path. If the file does not exist, it will be created.
     */
    public void load() {
        JsonObject loadedJson = readFile();
        if (loadedJson == null) {
            Utils.LOG.warn("Config " + clazz.getSimpleName() + " file not found, creating new one");
            value = ConfigHelper.createInstance(clazz);
            save();
            return;
        }

        try {
            BooleanRef errorHappened = new BooleanRef(false);
            Gson gson = createGson(errorHappened);
            value = gson.fromJson(loadedJson, clazz);
            if (errorHappened.get()) {
                save();
            }
        } catch (Exception e) {
            Utils.LOG.error("Failed to load config " + clazz.getSimpleName() + " from file", e);
            value = ConfigHelper.createInstance(clazz);
        }
    }

    @NotNull
    private Gson createGson(BooleanRef errorHappened) {
        ConfigTypeAdapterFactory factory = new ConfigTypeAdapterFactory(getName(), collectedClasses, errorHappened);
        return new GsonBuilder()
                .setExclusionStrategies(new ExclusionStrategyIgnore())
                .setPrettyPrinting()
                .registerTypeAdapterFactory(factory)
                .create();
    }

    public void save() {
        if (value == null) {
            throw new IllegalStateException("Config " + clazz.getSimpleName() + " has not been loaded before");
        }

        try (FileWriter writer = new FileWriter(path.toFile())) {
            BooleanRef ignored = new BooleanRef(false); // we don't need this in save
            createGson(ignored).toJson(value, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Nullable
    protected JsonObject readFile() {
        if (Files.exists(path, LinkOption.NOFOLLOW_LINKS)) {
            try (Reader reader = Files.newBufferedReader(path)) {
                return new Gson().fromJson(reader, JsonObject.class);
            } catch (Exception e) {
                Utils.LOG.error("Failed to load config from file " + path, e);
            }
        }
        return null;
    }

    /**
     * Collects all classes needed for the config type adapter.
     *
     * @param clazz     class to collect classes for
     * @param collector class collector to add classes to
     */
    protected void collectNeededClassTypes(Class<?> clazz, Set<Class<?>> collector) {
        Collection<Field> fields = ConfigHelper.getFields(clazz).values();
        for (Field field : fields) {
            Class<?> type = field.getType();
            if (type != Object.class && ConfigHelper.isUsableTypeForConfig(type)) {
                collector.add(type);
                collectNeededClassTypes(type, collector);
            }
        }
    }

}
