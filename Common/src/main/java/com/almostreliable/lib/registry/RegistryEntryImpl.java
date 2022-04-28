package com.almostreliable.lib.registry;

import net.minecraft.resources.ResourceLocation;

import javax.annotation.Nullable;
import java.util.Objects;

public class RegistryEntryImpl<T> implements RegistryEntry<T> {
    private final ResourceLocation registryName;
    @Nullable
    private T value;

    public RegistryEntryImpl(ResourceLocation registryName) {
        this.registryName = registryName;
    }

    @Override
    public T get() {
        if (value == null) {
            throw new IllegalStateException("RegistryEntry is not initialized yet");
        }

        return value;
    }

    @Override
    public void updateReference(T value) {
        Objects.requireNonNull(value);

        if (this.value != null) {
            throw new IllegalStateException("RegistryEntry cannot be updated twice");
        }

        this.value = value;
    }

    @Override
    public boolean isPresent() {
        return value != null;
    }

    @Override
    public ResourceLocation getRegistryName() {
        return registryName;
    }
}
