package com.github.almostreliable.lib.registry;

import com.github.almostreliable.lib.Utils;
import net.fabricmc.fabric.api.client.rendering.v1.BlockEntityRendererRegistry;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;

import java.util.Objects;

public class RegistryManagerFabric extends RegistryManager {
    public RegistryManagerFabric(String namespace) {
        super(namespace);
    }

    @Override
    @SuppressWarnings({ "unchecked" })
    protected <T> RegistryDelegate<T> getOrCreateDelegate(ResourceKey<Registry<T>> resourceKey) {
        return (RegistryDelegate<T>) registries.computeIfAbsent(resourceKey, key -> {
            Registry<T> vanillaRegistry = (Registry<T>) Registry.REGISTRY.get(key.location());
            Objects.requireNonNull(vanillaRegistry, "Something went wrong"); // TODO handle this?
            return new VanillaRegistryDelegate<>(vanillaRegistry);
        });
    }

    @Override
    public void initClient() {
        blockEntityRenderers.forEach((registryEntry, provider) -> {
            BlockEntityRendererRegistry.register(registryEntry.get(), Utils.cast(provider));
        });
    }
}
