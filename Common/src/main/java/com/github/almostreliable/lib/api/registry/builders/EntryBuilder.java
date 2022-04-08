package com.github.almostreliable.lib.api.registry.builders;

import com.github.almostreliable.lib.api.registry.RegistryEntry;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;

public interface EntryBuilder<T, BASE> {
    ResourceKey<Registry<BASE>> getRegistryKey();

    T create();

    RegistryEntry<T> register();

    String getName();
}
