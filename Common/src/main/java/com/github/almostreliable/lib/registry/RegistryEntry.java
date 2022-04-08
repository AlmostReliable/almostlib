package com.github.almostreliable.lib.registry;

import net.minecraft.resources.ResourceLocation;

import java.util.function.Supplier;

public interface RegistryEntry<T> extends Supplier<T> {
    void updateReference(T value);

    boolean isPresent();

    ResourceLocation getRegistryName();
}
