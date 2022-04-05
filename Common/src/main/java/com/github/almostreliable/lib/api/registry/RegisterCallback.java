package com.github.almostreliable.lib.api.registry;

import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;

import java.util.function.Supplier;

@FunctionalInterface
public interface RegisterCallback {
    <T, BASE> Supplier<T> onFinishRegister(String id, Supplier<T> entrySupplier, ResourceKey<Registry<BASE>> resourceKey);
}
