package com.almostreliable.almostlib.registry;

import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;

import javax.annotation.Nullable;
import java.util.Optional;
import java.util.function.Supplier;

public class RegistryEntryImpl<T> implements RegistryEntry<T> {

    private final ResourceLocation id;
    private final ResourceKey<T> key;
    private final Registry<T> registry;

    /* Initialize stuff */
    @Nullable private Supplier<T> supplier;
    @Nullable private T value;

    public RegistryEntryImpl(Registry<T> registry, ResourceLocation id, Supplier<T> supplier) {
        this.id = id;
        this.supplier = supplier;
        this.registry = registry;
        this.key = ResourceKey.create(registry.key(), id);
    }

    @Override
    public ResourceLocation getId() {
        return id;
    }

    @Override
    public ResourceKey<T> getKey() {
        return key;
    }

    @Override
    public Optional<Holder<T>> asHolder() {
        return registry.getHolder(this.key);
    }

    @Override
    public T get() {
        if (value != null) {
            return value;
        }

        if (supplier != null) {
            value = supplier.get();
            supplier = null;
            return value;
        }

        throw new IllegalStateException("No value for " + id);
    }
}
