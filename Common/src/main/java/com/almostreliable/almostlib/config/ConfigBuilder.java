package com.almostreliable.almostlib.config;

import com.electronwill.nightconfig.core.CommentedConfig;
import com.electronwill.nightconfig.core.UnmodifiableConfig;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

public class ConfigBuilder {

    private final CommentedConfig config;
    private final List<String> path;
    private final AtomicBoolean requiresSave;

    ConfigBuilder() {
        this.config = CommentedConfig.inMemoryConcurrent();
        this.requiresSave = new AtomicBoolean(true);
        this.path = List.of();
    }

    ConfigBuilder(CommentedConfig config) {
        this.config = config;
        this.requiresSave = new AtomicBoolean(false);
        this.path = List.of();
    }

    ConfigBuilder(ConfigBuilder parent, List<String> subPath) {
        this.config = parent.config;
        this.requiresSave = parent.requiresSave;
        this.path = subPath;
    }

    void markDirty() {
        this.requiresSave.set(true);
    }

    @Nullable
    Object getRaw(List<String> path) {
        return config.get(path);
    }

    void set(List<String> path, Object value) {
        config.set(path, value);
    }

    void setComment(List<String> path, String comment) {
        String[] commentLines = comment.trim().split("\n");
        comment = Arrays.stream(commentLines).map(s -> " " + s).collect(Collectors.joining("\n"));
        String oldComment = Optional.ofNullable(config.getComment(path)).orElse("");
        if (!comment.equals(oldComment)) {
            markDirty();
        }

        if (!comment.isEmpty()) {
            config.setComment(path, comment);
        }
    }

    UnmodifiableConfig getConfig() {
        return config;
    }

    List<String> createPath(String subPath) {
        List<String> path = new ArrayList<>(this.path);
        path.add(subPath);
        return path;
    }

    public ValueSpec<String> value(String name, String defaultValue) {
        return new ValueSpec<>(this, createPath(name), defaultValue, Conversions::toString);
    }

    public RangeSpec<Integer> intValue(String name, int defaultValue) {
        return new RangeSpec<>(this, createPath(name), defaultValue, Conversions::toInt);
    }

    public RangeSpec<Integer> intValue(String name) {
        return intValue(name, 0);
    }

    public RangeSpec<Long> longValue(String name, long defaultValue) {
        return new RangeSpec<>(this, createPath(name), defaultValue, Conversions::toLong);
    }

    public RangeSpec<Long> longValue(String name) {
        return longValue(name, 0);
    }

    public RangeSpec<Float> floatValue(String name, float defaultValue) {
        return new RangeSpec<>(this, createPath(name), defaultValue, Conversions::toFloat);
    }

    public RangeSpec<Float> floatValue(String name) {
        return floatValue(name, 0);
    }

    public RangeSpec<Double> doubleValue(String name, double defaultValue) {
        return new RangeSpec<>(this, createPath(name), defaultValue, Conversions::toDouble);
    }

    public RangeSpec<Double> doubleValue(String name) {
        return doubleValue(name, 0);
    }

    public ValueSpec<Boolean> booleanValue(String name, boolean defaultValue) {
        return new ValueSpec<>(this, createPath(name), defaultValue, Conversions::toBoolean);
    }

    public <T extends Enum<T>> ValueSpec<T> enumValue(String name, Class<T> enumType, T defaultValue) {
        var spec = new ValueSpec<>(this, createPath(name), defaultValue, o -> Conversions.toEnum(o, enumType));
        spec.possibleValues((Object[]) enumType.getEnumConstants());
        return spec;
    }

    public ConfigBuilder category(String name, String comment) {
        List<String> subPath = createPath(name);
        setComment(subPath, comment);
        return new ConfigBuilder(this, subPath);
    }

    public boolean requiresSave() {
        return requiresSave.get();
    }
}
