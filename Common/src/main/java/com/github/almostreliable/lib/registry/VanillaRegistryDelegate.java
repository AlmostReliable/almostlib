package com.github.almostreliable.lib.registry;

import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;

public class VanillaRegistryDelegate<T> extends AbstractRegistryDelegate<T> {
    private final Registry<T> registry;

    public VanillaRegistryDelegate(Registry<T> registry) {
        this.registry = registry;
    }

    @Override
    public void init(String namespace) {
        for (var entry : entries.entrySet()) {
            ResourceLocation location = new ResourceLocation(namespace, entry.getKey());
            Registry.register(registry, location, entry.getValue().get());
        }
    }
}
