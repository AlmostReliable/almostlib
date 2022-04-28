package com.almostreliable.lib.registry.builders;

import com.almostreliable.lib.registry.RegistryEntry;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;

public interface RegistryEntryBuilder<T, BASE> {
    ResourceKey<Registry<BASE>> getRegistryKey();

    T create();

    RegistryEntry<T> register();

    String getName();

    /**
     * Will be called after the manager registers the entry. At this state the entry is not resolved!
     *
     * @param registryEntry the entry which was registered
     */
    void onRegister(RegistryEntry<T> registryEntry); // TODO maybe move this into a callback after the entries getting registered into minecraft registries
}
