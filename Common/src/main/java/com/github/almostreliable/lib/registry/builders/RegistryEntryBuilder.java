package com.github.almostreliable.lib.registry.builders;

import com.github.almostreliable.lib.registry.RegistryEntry;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;

public interface RegistryEntryBuilder<T, BASE> {
    ResourceKey<Registry<BASE>> getRegistryKey();

    T create();

    RegistryEntry<T> register();

    String getName();
}
