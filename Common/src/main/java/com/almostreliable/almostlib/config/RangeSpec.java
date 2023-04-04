package com.almostreliable.almostlib.config;

import javax.annotation.Nullable;
import java.util.List;
import java.util.function.Function;

public class RangeSpec<T extends Comparable<T>> extends ValueSpec<T> {

    @Nullable private T minValue;
    @Nullable private T maxValue;

    RangeSpec(ConfigBuilder owner, List<String> path, T defaultValue, Function<Object, T> rawValueConverter) {
        super(owner, path, defaultValue, rawValueConverter);
    }

    public RangeSpec<T> range(T minValue, T maxValue) {
        this.minValue = minValue;
        this.maxValue = maxValue;
        return this;
    }

    public RangeSpec<T> min(T minValue) {
        this.minValue = minValue;
        return this;
    }

    public RangeSpec<T> max(T maxValue) {
        this.maxValue = maxValue;
        return this;
    }

    @Override
    protected T convertValue(Object rawValue) {
        T value = super.convertValue(rawValue);

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
}
