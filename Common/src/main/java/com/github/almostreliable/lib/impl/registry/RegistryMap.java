package com.github.almostreliable.lib.impl.registry;

import com.github.almostreliable.lib.api.AlmostLib;
import com.github.almostreliable.lib.api.registry.IAlmostRegistry;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;

import java.util.HashMap;
import java.util.Map;

public class RegistryMap {

    private Map<ResourceKey<?>, IAlmostRegistry<?>> registries = new HashMap<>();

    public <T> IAlmostRegistry<T> getOrCreate(String namespace, ResourceKey<Registry<T>> resourceKey) {
        //noinspection unchecked
        return (IAlmostRegistry<T>) registries.computeIfAbsent(resourceKey,
                registryResourceKey -> AlmostLib.INSTANCE.createRegistry(namespace, resourceKey));
    }
}
