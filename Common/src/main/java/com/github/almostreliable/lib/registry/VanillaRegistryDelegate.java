package com.github.almostreliable.lib.registry;

import com.github.almostreliable.lib.api.registry.RegistryDelegate;
import com.google.common.base.Suppliers;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.Supplier;

public class VanillaRegistryDelegate<T> implements RegistryDelegate<T> {
    protected final Map<ResourceLocation, Supplier<T>> entries = new HashMap<>();
    private final Registry<T> registry;

    public VanillaRegistryDelegate(Registry<T> registry) {
        this.registry = registry;
    }

    @Override
    public <E extends T> Supplier<E> register(String namespace, String id, Supplier<? extends E> supplier) {
        Objects.requireNonNull(id);
        Objects.requireNonNull(supplier);

        ResourceLocation fullId = new ResourceLocation(namespace, id);
        Supplier<E> entry = Suppliers.memoize(supplier::get);

        if (entries.containsKey(fullId)) {
            throw new IllegalArgumentException("Duplicate entry registration: " + fullId);
        }

        //noinspection unchecked
        entries.put(fullId, (Supplier<T>) entry);
        return entry;
    }

    @Override
    public boolean isPresent() {
        return !entries.isEmpty();
    }

    @Override
    public void init() {
        for (var entry : entries.entrySet()) {
            Registry.register(registry, entry.getKey(), entry.getValue().get());
        }
    }
}
