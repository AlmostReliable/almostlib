package com.github.almostreliable.lib.registry;

import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;

import javax.annotation.Nullable;
import java.util.function.Supplier;

public interface EntryLookup {
    @Nullable
    <E> Supplier<?> getEntry(ResourceKey<Registry<E>> resourceKey, String id);
}
