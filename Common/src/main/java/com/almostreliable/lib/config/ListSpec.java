package com.almostreliable.lib.config;

import javax.annotation.Nullable;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Supplier;

public class ListSpec<T> extends ValueSpec<List<T>> {

    private static final int DEACTIVATED = -1;
    private int limit = DEACTIVATED;
    private int forceLength = DEACTIVATED;
    @Nullable private Supplier<T> forceLengthFillValue;

    protected ListSpec(ConfigBuilder owner, List<String> path, List<T> defaultValue, Class<T> elementType, Function<Object, T> rawElementConverter) {
        super(owner, path, defaultValue, o -> Conversions.toList(o, elementType, rawElementConverter));
    }

    public ListSpec<T> forceLength(int length, Supplier<T> defaultFillValue) {
        forceLength = length;
        forceLengthFillValue = defaultFillValue;
        return this;
    }

    public ListSpec<T> limit(int limit) {
        this.limit = limit;
        return this;
    }

    @Override
    protected List<T> convertValue(Object rawValue) {
        List<T> value = super.convertValue(rawValue);

        if (forceLength != DEACTIVATED && value.size() < forceLength && forceLengthFillValue != null) {
            owner.markDirty();
            while (value.size() < forceLength) {
                value.add(forceLengthFillValue.get());
            }
        }

        if (limit != DEACTIVATED && value.size() > limit) {
            owner.markDirty();
            return value.subList(0, limit);
        }

        return value;
    }

    /**
     * Read the config value as a set instead of a list. This will preserve order in the config file, but not in the result set.
     *
     * @return the config value as a set
     */
    public Set<T> readAsSet() {
        return new HashSet<>(read());
    }
}
