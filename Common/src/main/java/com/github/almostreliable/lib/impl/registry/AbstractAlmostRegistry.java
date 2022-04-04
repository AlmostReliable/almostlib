package com.github.almostreliable.lib.impl.registry;

import com.github.almostreliable.lib.api.registry.IAlmostRegistry;
import com.google.common.base.Suppliers;
import net.minecraft.resources.ResourceLocation;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.Supplier;

public abstract class AbstractAlmostRegistry<T> implements IAlmostRegistry<T> {
    protected final Map<ResourceLocation, Supplier<T>> entries = new HashMap<>();
    protected final String namespace;

    public AbstractAlmostRegistry(String namespace) {
        this.namespace = namespace;
    }

    @Override
    public <E extends T> Supplier<E> register(String id, Supplier<E> supplier) {
        Objects.requireNonNull(id);
        Objects.requireNonNull(supplier);

        ResourceLocation fullId = new ResourceLocation(namespace, id);
        Supplier<E> entry = Suppliers.memoize(supplier::get);

        if(entries.containsKey(fullId)) {
            throw new IllegalArgumentException("Duplicate entry registration: " + fullId);
        }

        //noinspection unchecked
        entries.put(fullId, (Supplier<T>) entry);
        return entry;
    }
}
