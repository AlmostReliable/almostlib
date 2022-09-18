package com.almostreliable.almostlib.registry;

import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;

import java.util.Optional;
import java.util.function.Supplier;

public class RegistryEntryImpl<T> implements RegistryEntry<T> {

    public final ResourceLocation id;
    public final ResourceKey<T> key;
    public final Supplier<T> supplier;
    private final Registry<T> registry;

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
    public Optional<Holder<T>> asHolder() {
        return registry.getHolder(this.key);
    }

    @Override
    public T get() {
        return supplier.get();
    }
}
