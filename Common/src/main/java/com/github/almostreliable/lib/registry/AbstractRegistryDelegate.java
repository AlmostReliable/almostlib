package com.github.almostreliable.lib.registry;

import com.github.almostreliable.lib.api.registry.RegistryDelegate;
import com.google.common.base.Suppliers;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.Supplier;

public abstract class AbstractRegistryDelegate<T> implements RegistryDelegate<T> {
    protected final Map<String, Supplier<T>> entries = new HashMap<>();

    @Override
    public <E extends T> Supplier<E> register(String id, Supplier<? extends E> supplier) {
        Objects.requireNonNull(id);
        Objects.requireNonNull(supplier);

        if (entries.containsKey(id)) {
            throw new IllegalArgumentException("Duplicate entry registration: " + id);
        }

        Supplier<E> entry = Suppliers.memoize(supplier::get);

        //noinspection unchecked
        entries.put(id, (Supplier<T>) entry);
        return entry;
    }

    @Override
    public boolean isPresent() {
        return !entries.isEmpty();
    }

    @SuppressWarnings("unchecked")
    @Override
    @Nullable
    public <E extends T> Supplier<E> find(String id) {
        return (Supplier<E>) entries.get(id);
    }
}
