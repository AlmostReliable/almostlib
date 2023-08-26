package com.almostreliable.lib.config;

import javax.annotation.Nullable;
import java.util.List;
import java.util.function.Function;

/**
 * A config entry that limits a value to a certain range.
 * <p>
 * Automatically attaches an information comment when a range is specified.<br>
 * This can be disabled with {@link #noRangeComment()}.
 *
 * @param <T> The type of the range entry.
 */
public class RangeSpec<T extends Comparable<T>> extends ValueSpec<T> {

    @Nullable protected T minValue;
    @Nullable protected T maxValue;
    private boolean noRangeComment;

    protected RangeSpec(ConfigBuilder owner, List<String> path, T defaultValue, Function<Object, T> rawValueConverter) {
        super(owner, path, defaultValue, rawValueConverter);
    }

    /**
     * Ensures the value is within the given range.
     * <p>
     * Both bounds are inclusive.
     *
     * @param minValue The minimum value.
     * @param maxValue The maximum value.
     * @return The instance of the spec.
     */
    public RangeSpec<T> range(T minValue, T maxValue) {
        this.minValue = minValue;
        this.maxValue = maxValue;
        return this;
    }

    /**
     * Ensures the value is greater than or equal to the given value.
     *
     * @param minValue The minimum value.
     * @return The instance of the spec.
     */
    public RangeSpec<T> min(T minValue) {
        this.minValue = minValue;
        return this;
    }

    /**
     * Ensures the value is less than or equal to the given value.
     *
     * @param maxValue The maximum value.
     * @return The instance of the spec.
     */
    public RangeSpec<T> max(T maxValue) {
        this.maxValue = maxValue;
        return this;
    }

    /**
     * Disables the range comment.
     * <p>
     * The range comment is an automatically generated comment that
     * shows the allowed range of the value if specified.
     *
     * @return The instance of the spec.
     */
    public final RangeSpec<T> noRangeComment() {
        noRangeComment = true;
        return this;
    }

    @Override
    public RangeSpec<T> valueComment(Object... values) {
        throw new UnsupportedOperationException("Value comments are not supported in RangeSpecs");
    }

    @Override
    protected T convertValue(Object rawValue) {
        T value = super.convertValue(rawValue);

        if (value == null) {
            throw new IllegalArgumentException("Could not convert " + rawValue + " to " + defaultValue.getClass().getSimpleName());
        }

        if (minValue != null && value.compareTo(minValue) < 0) {
            owner.markDirty();
            return minValue;
        }

        if (maxValue != null && value.compareTo(maxValue) > 0) {
            owner.markDirty();
            return maxValue;
        }

        return value;
    }

    @Override
    protected String generateComment() {
        String c = super.generateComment();
        if (noRangeComment) return c;
        if (minValue == null && maxValue == null) return c;

        return c + "\nRange: " + generateRangeString();
    }

    private String generateRangeString() {
        if (minValue == null) return "≤" + maxValue;
        if (maxValue == null) return "≥" + minValue;
        return minValue + " to " + maxValue;
    }
}
