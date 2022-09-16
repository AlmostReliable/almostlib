package com.almostreliable.almostlib.registry;

import net.minecraft.resources.ResourceLocation;

import java.util.function.Supplier;

public interface RegistryEntry<T> extends Supplier<T> {
    ResourceLocation getId();
}
