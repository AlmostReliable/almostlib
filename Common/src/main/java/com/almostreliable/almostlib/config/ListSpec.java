package com.almostreliable.almostlib.config;

import java.util.List;
import java.util.function.Function;

public class ListSpec<T> extends ValueSpec<List<T>> {

    private static final int NO_LIMIT = -1;
    private int limit = NO_LIMIT;

    protected ListSpec(ConfigBuilder owner, List<String> path, List<T> defaultValue, Class<T> elementType, Function<Object, T> rawElementConverter) {
        super(owner, path, defaultValue, o -> Conversions.toList(o, elementType, rawElementConverter));
    }

    public ListSpec<T> limit(int limit) {
        this.limit = limit;
        return this;
    }

    @Override
    protected List<T> convertValue(Object rawValue) {
        List<T> value = super.convertValue(rawValue);

        if (limit != NO_LIMIT && value.size() > limit) {
            owner.markDirty();
            return value.subList(0, limit);
        }

        return value;
    }
}
