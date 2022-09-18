package com.almostreliable.almostlib.registry;

import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;

import java.util.Optional;
import java.util.function.Supplier;

public interface RegistryEntry<T> extends Supplier<T> {
    ResourceLocation getId();

    Optional<Holder<T>> asHolder();

    default Holder<T> asHolderOrThrow() {
        return asHolder().orElseThrow(() -> new IllegalStateException("No holder for " + getId()));
    }
}
