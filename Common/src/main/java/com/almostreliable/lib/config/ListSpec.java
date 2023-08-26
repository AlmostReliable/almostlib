package com.almostreliable.lib.config;

import javax.annotation.Nullable;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * A config entry that allows a list of values.
 * <p>
 * Automatically attaches an information comment when a length limitation is specified.<br>
 * This can be disabled with {@link #noLengthComment()}.
 *
 * @param <T> The type of the list entry.
 */
public class ListSpec<T> extends ValueSpec<List<T>> {

    private static final int DEACTIVATED = -1;

    protected int minLength = DEACTIVATED;
    protected int maxLength = DEACTIVATED;
    @Nullable protected Supplier<T> minLengthFillValue;
    private boolean noLengthComment;

    protected ListSpec(ConfigBuilder owner, List<String> path, List<T> defaultValue, Class<T> elementType, Function<Object, T> rawElementConverter) {
        super(owner, path, defaultValue, o -> Converters.toList(o, elementType, rawElementConverter));
    }

    /**
     * Ensures the list has the given minimum length.
     * <p>
     * If the list is shorter than the given length, it will be filled with the default fill value.
     *
     * @param length    The minimum length of the list.
     * @param fillValue The default fill value.
     * @return The instance of the spec.
     */
    public ListSpec<T> minLength(int length, Supplier<T> fillValue) {
        minLength = length;
        minLengthFillValue = fillValue;
        return this;
    }

    /**
     * Ensures the list has the given maximum length.
     * <p>
     * If the list is longer than the given length, it will be truncated.
     *
     * @param limit The maximum length of the list.
     * @return The instance of the spec.
     */
    public ListSpec<T> maxLength(int limit) {
        this.maxLength = limit;
        return this;
    }

    /**
     * Disables the length comment.
     * <p>
     * The length comment is an automatically generated comment that
     * shows the allowed length of the list if specified.
     *
     * @return The instance of the spec.
     */
    public ListSpec<T> noLengthComment() {
        noLengthComment = true;
        return this;
    }

    /**
     * Returns the config value as a Set instead of List.
     * <p>
     * This will preserve the order in the config file, but not in the result set.
     *
     * @return The config value as a Set.
     */
    public Set<T> readAsSet() {
        return new HashSet<>(read());
    }

    @Override
    protected List<T> convertValue(Object rawValue) {
        List<T> value = super.convertValue(rawValue);

        if (value == null) {
            throw new IllegalArgumentException("Could not convert " + rawValue + " to list");
        }

        if (minLength != DEACTIVATED && value.size() < minLength && minLengthFillValue != null) {
            owner.markDirty();
            while (value.size() < minLength) {
                value.add(minLengthFillValue.get());
            }
        }

        if (maxLength != DEACTIVATED && value.size() > maxLength) {
            owner.markDirty();
            return value.subList(0, maxLength);
        }

        return value;
    }

    @Override
    protected String generateComment() {
        String c = super.generateComment();
        if (noLengthComment) return c;
        if (minLength == DEACTIVATED && maxLength == DEACTIVATED) return c;

        c = c + "\nLength: " + generateLengthString();
        if (minLengthFillValue != null) {
            c = c + "\nDefault Fill Value: " + minLengthFillValue.get();
        }
        return c;
    }

    private String generateLengthString() {
        if (minLength == DEACTIVATED) return "≤" + maxLength;
        if (maxLength == DEACTIVATED) return "≥" + minLength;
        return minLength + " to " + maxLength;
    }
}
