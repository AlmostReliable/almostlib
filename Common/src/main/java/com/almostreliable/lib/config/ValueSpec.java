package com.almostreliable.lib.config;

import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * The base class for all config entries.
 * <p>
 * A ValueSpec defines a simple value entry in the config. It has no special behavior.
 *
 * @param <T> The type of the value entry.
 */
public class ValueSpec<T> {

    protected final ConfigBuilder owner;
    protected final List<String> path;
    protected final T defaultValue;
    protected final Function<Object, T> rawValueConverter;

    @Nullable private String comment;
    @Nullable private String valueComment;

    protected ValueSpec(ConfigBuilder owner, List<String> path, T defaultValue, Function<Object, T> rawValueConverter) {
        this.owner = owner;
        this.path = path;
        this.defaultValue = defaultValue;
        this.rawValueConverter = rawValueConverter;
    }

    /**
     * Sets the comment for this entry.
     * <p>
     * There can only be one comment per entry. Calling this method multiple
     * times will override the previous comment.
     * <p>
     * This method doesn't handle wrapping the comment. Make sure to use
     * proper line breaks with {@code \n} to keep the config readable.
     *
     * @param comment The comment for this entry.
     * @return The instance of the entry spec.
     */
    public ValueSpec<T> comment(String comment) {
        this.comment = comment.trim();
        return this;
    }

    /**
     * Sets the possible values for this entry.
     * <p>
     * This method will generate a comment that shows the possible values for this entry.<br>
     * It will be appended to the comment set by {@link #comment(String)}.
     *
     * @param values The possible values for this entry.
     * @return The instance of the entry spec.
     */
    public ValueSpec<T> valueComment(Object... values) {
        String strValues = Arrays.stream(values).map(o -> "\"" + o + "\"").collect(Collectors.joining(", "));
        valueComment = "Possible Values: " + strValues;
        return this;
    }

    /**
     * Reads the raw value from the config and handles the conversion
     * to the target type.
     * <p>
     * If the value couldn't be converted, the default value will be
     * applied and the config will be marked as dirty.
     * <p>
     * This needs to be called on every entry spec in order to
     * be initialized properly.
     *
     * @return The converted value of the current entry spec.
     */
    public final T read() {
        try {
            Object rawValue = owner.getRaw(path);
            if (rawValue == null) {
                throw new IllegalArgumentException("Could not find value at path " + path);
            }

            T value = convertValue(rawValue);
            if (value == null) {
                throw new IllegalArgumentException("Could not convert " + rawValue + " to " + defaultValue.getClass().getSimpleName());
            }

            onRead();
            owner.set(path, value);
            return value;
        } catch (IllegalArgumentException | ClassCastException e) {
            owner.markDirty();
            onRead();
            owner.set(path, defaultValue);
            return defaultValue;
        }
    }

    /**
     * Converts the raw value from the config to the target type of the spec.
     * <p>
     * The method may throw an exception for invalid values.<br>
     * When thrown, the config will be marked as dirty and the default value will be applied.
     *
     * @param rawValue The raw value from the config to convert.
     * @return The converted value.
     * @throws IllegalArgumentException If the passed value doesn't have the expected type.
     * @throws ClassCastException       If the passed value can't be cast to the expected type.
     */
    @Nullable
    protected T convertValue(Object rawValue) throws IllegalArgumentException, ClassCastException {
        return rawValueConverter.apply(rawValue);
    }

    /**
     * Called after the value has been read from the config,
     * but before it has been set.
     */
    protected void onRead() {
        owner.setComment(path, generateComment());
    }

    /**
     * Generates the comment for this entry that is applied
     * in {@link #onRead()}.
     * <p>
     * The comment set by {@link #comment(String)} can be retrieved by
     * calling super on this method. The super comment should always
     * be the first line.
     * <p>
     * If the custom spec doesn't support value comments, override
     * the {@link #valueComment(Object...)} method and throw an
     * {@link UnsupportedOperationException}.
     *
     * @return The comment for this entry.
     */
    protected String generateComment() {
        String c = comment == null ? "" : comment;
        if (valueComment != null) {
            c += "\n" + valueComment;
        }
        return c;
    }
}
