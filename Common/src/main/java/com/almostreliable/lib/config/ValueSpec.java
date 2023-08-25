package com.almostreliable.lib.config;

import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

@SuppressWarnings("UnusedReturnValue")
public class ValueSpec<T> {

    protected final List<String> path;
    protected final T defaultValue;
    protected final Function<Object, T> rawValueConverter;
    protected final ConfigBuilder owner;
    @Nullable private String comment;
    @Nullable private String valueComment;

    protected ValueSpec(ConfigBuilder owner, List<String> path, T defaultValue, Function<Object, T> rawValueConverter) {
        this.owner = owner;
        this.path = path;
        this.defaultValue = defaultValue;
        this.rawValueConverter = rawValueConverter;
    }

    public ValueSpec<T> comment(String comment) {
        this.comment = comment.trim();
        return this;
    }

    public final ValueSpec<T> possibleValues(Object... values) {
        String strValues = Arrays.stream(values).map(o -> "\"" + o + "\"").collect(Collectors.joining(", "));
        valueComment = "Possible values: " + strValues;
        return this;
    }

    /**
     * Convert the raw value from the config. Method may throw an exception for invalid values. <br>
     * If an exception is thrown, the config will be marked as dirty and the default value will be returned. See {@link #read()}. <br>
     * <p>
     * Method may also call `owner.markDirty()` if the value is invalid.
     *
     * @return the raw value from the config
     */
    @Nullable
    protected T convertValue(Object rawValue) {
        return rawValueConverter.apply(rawValue);
    }

    public final T read() {
        try {
            Object raw = owner.getRaw(path);
            if (raw == null) {
                throw new RuntimeException("Value not found");
            }

            T value = convertValue(raw);
            if (value == null) {
                throw new RuntimeException("Value is null");
            }

            onRead();
            owner.set(path, value);
            return value;
        } catch (RuntimeException e) {
            owner.markDirty();
            onRead();
            owner.set(path, defaultValue);
            return defaultValue;
        }
    }

    protected void onRead() {
        String c = comment == null ? "" : comment;
        if (valueComment != null) {
            c += "\n" + valueComment;
        }
        owner.setComment(path, c);
    }
}
