package com.github.almostreliable.lib.registry;

import net.minecraft.resources.ResourceLocation;

import java.util.function.Supplier;

public class RegistryEntryData<T> {
    private final RegistryEntry<T> registryEntry;
    private final Supplier<T> factory;

    RegistryEntryData(RegistryEntry<T> registryEntry, Supplier<T> factory) {
        this.registryEntry = registryEntry;
        this.factory = factory;
    }

    public static <T> RegistryEntryData<T> of(ResourceLocation registryName, Supplier<T> factory) {
        RegistryEntry<T> registryEntry = new RegistryEntryImpl<>(registryName);
        return new RegistryEntryData<>(registryEntry, factory);
    }

    public RegistryEntry<T> getRegistryEntry() {
        return registryEntry;
    }

    public ResourceLocation getRegistryName() {
        return getRegistryEntry().getRegistryName();
    }

    public Supplier<T> getFactory() {
        return factory;
    }
}
