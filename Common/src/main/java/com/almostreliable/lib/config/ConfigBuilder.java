package com.almostreliable.lib.config;

import com.electronwill.nightconfig.core.CommentedConfig;
import com.electronwill.nightconfig.core.UnmodifiableConfig;
import com.electronwill.nightconfig.toml.TomlFormat;

import javax.annotation.Nullable;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Function;
import java.util.stream.Collectors;

public class ConfigBuilder {

    private final CommentedConfig config;
    private final List<String> paths;
    private final AtomicBoolean requiresSave;

    static CommentedConfig defaultConfig() {
        return CommentedConfig.of(() -> Collections.synchronizedMap(new LinkedHashMap<>()), TomlFormat.instance());
    }

    ConfigBuilder() {
        this.config = defaultConfig();
        this.paths = List.of();
        this.requiresSave = new AtomicBoolean(true);
    }

    ConfigBuilder(CommentedConfig config) {
        this.config = config;
        this.paths = List.of();
        this.requiresSave = new AtomicBoolean(false);
    }

    private ConfigBuilder(ConfigBuilder parent, List<String> subPath) {
        this.config = parent.config;
        this.paths = subPath;
        this.requiresSave = parent.requiresSave;
    }

    /**
     * Gets the raw value at the given path.
     *
     * @param path The path to the value.
     * @return The raw value.
     */
    @Nullable
    Object getRaw(List<String> path) {
        return config.get(path);
    }

    /**
     * Sets the value at the given path.
     *
     * @param path  The path to the value.
     * @param value The value to set.
     */
    void set(List<String> path, Object value) {
        config.set(path, value);
    }

    /**
     * Marks this config as dirty, meaning it needs to be saved.
     */
    void markDirty() {
        requiresSave.set(true);
    }

    /**
     * Checks if this config needs to be saved.
     *
     * @return True if this config needs to be saved, false otherwise.
     */
    boolean requiresSave() {
        return requiresSave.get();
    }

    /**
     * Sets the comment at the given path.
     * <p>
     * The comment will be split at newlines, trimmed and prefixed with a space.<br>
     * Setting the comment will be skipped if it matches the current comment or if
     * it's empty after transformation.
     *
     * @param path    The path to the value.
     * @param comment The comment to set.
     */
    void setComment(List<String> path, String comment) {
        String[] commentLines = comment.trim().split("\n");
        String transformedComment = Arrays.stream(commentLines).map(s -> " " + s).collect(Collectors.joining("\n"));
        String oldComment = Optional.ofNullable(config.getComment(path)).orElse("");
        if (!transformedComment.equals(oldComment)) {
            markDirty();
        }

        if (!transformedComment.isEmpty()) {
            config.setComment(path, transformedComment);
        }
    }

    UnmodifiableConfig getConfig() {
        return config;
    }

    public <T> ValueSpec<T> value(String name, T defaultValue, Function<Object, T> converter) {
        return new ValueSpec<>(this, createPath(name), defaultValue, converter);
    }

    public ValueSpec<String> stringValue(String name, String defaultValue) {
        return value(name, defaultValue, Converters::toString);
    }

    public ValueSpec<String> stringValue(String name) {
        return stringValue(name, "");
    }

    public ValueSpec<Boolean> booleanValue(String name, boolean defaultValue) {
        return value(name, defaultValue, Converters::toBoolean);
    }

    public ValueSpec<Boolean> booleanValue(String name) {
        return booleanValue(name, false);
    }

    public RangeSpec<Integer> intValue(String name, int defaultValue) {
        return new RangeSpec<>(this, createPath(name), defaultValue, Converters::toInt);
    }

    public RangeSpec<Integer> intValue(String name) {
        return intValue(name, 0);
    }

    public RangeSpec<Long> longValue(String name, long defaultValue) {
        return new RangeSpec<>(this, createPath(name), defaultValue, Converters::toLong);
    }

    public RangeSpec<Long> longValue(String name) {
        return longValue(name, 0);
    }

    public RangeSpec<Float> floatValue(String name, float defaultValue) {
        return new RangeSpec<>(this, createPath(name), defaultValue, Converters::toFloat);
    }

    public RangeSpec<Float> floatValue(String name) {
        return floatValue(name, 0);
    }

    public RangeSpec<Double> doubleValue(String name, double defaultValue) {
        return new RangeSpec<>(this, createPath(name), defaultValue, Converters::toDouble);
    }

    public RangeSpec<Double> doubleValue(String name) {
        return doubleValue(name, 0);
    }

    public <T extends Enum<T>> ValueSpec<T> enumValue(String name, Class<T> clazz, T defaultValue) {
        var spec = new ValueSpec<>(this, createPath(name), defaultValue, o -> Converters.toEnum(o, clazz));
        spec.valueComment((Object[]) clazz.getEnumConstants());
        return spec;
    }

    public <T extends Enum<T>> ValueSpec<T> enumValue(String name, Class<T> clazz) {
        return enumValue(name, clazz, clazz.getEnumConstants()[0]);
    }

    public <T> ListSpec<T> listValue(String name, Class<T> clazz, List<T> defaultValue, Function<Object, T> elementConverter) {
        return new ListSpec<>(this, createPath(name), defaultValue, clazz, elementConverter);
    }

    public ListSpec<String> stringListValue(String name, List<String> defaultValue) {
        return listValue(name, String.class, defaultValue, Converters::toString);
    }

    public ListSpec<String> stringListValue(String name) {
        return stringListValue(name, List.of());
    }

    public ListSpec<Boolean> booleanListValue(String name, List<Boolean> defaultValue) {
        return listValue(name, Boolean.class, defaultValue, Converters::toBoolean);
    }

    public ListSpec<Boolean> booleanListValue(String name) {
        return booleanListValue(name, List.of());
    }

    public ListSpec<Integer> intListValue(String name, List<Integer> defaultValue) {
        return listValue(name, Integer.class, defaultValue, Converters::toInt);
    }

    public ListSpec<Integer> intListValue(String name) {
        return intListValue(name, List.of());
    }

    public ListSpec<Long> longListValue(String name, List<Long> defaultValue) {
        return listValue(name, Long.class, defaultValue, Converters::toLong);
    }

    public ListSpec<Long> longListValue(String name) {
        return longListValue(name, List.of());
    }

    public ListSpec<Float> floatListValue(String name, List<Float> defaultValue) {
        return listValue(name, Float.class, defaultValue, Converters::toFloat);
    }

    public ListSpec<Float> floatListValue(String name) {
        return floatListValue(name, List.of());
    }

    public ListSpec<Double> doubleListValue(String name, List<Double> defaultValue) {
        return listValue(name, Double.class, defaultValue, Converters::toDouble);
    }

    public ListSpec<Double> doubleListValue(String name) {
        return doubleListValue(name, List.of());
    }

    public <T extends Enum<T>> ListSpec<T> enumListValue(String name, Class<T> clazz, List<T> defaultValue) {
        return listValue(name, clazz, defaultValue, o -> Converters.toEnum(o, clazz));
    }

    public <T extends Enum<T>> ListSpec<T> enumListValue(String name, Class<T> clazz) {
        return enumListValue(name, clazz, List.of());
    }

    public ConfigBuilder category(String name, String comment) {
        List<String> subPath = createPath(name);
        setComment(subPath, comment);
        return new ConfigBuilder(this, subPath);
    }

    private List<String> createPath(String subPath) {
        List<String> path = new ArrayList<>(paths);
        path.add(subPath);
        return path;
    }
}
