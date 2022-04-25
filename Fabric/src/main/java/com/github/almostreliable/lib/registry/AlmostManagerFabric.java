package com.github.almostreliable.lib.registry;

import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;

import java.util.Objects;

public class AlmostManagerFabric extends AlmostManager {

    public AlmostManagerFabric(String namespace) {
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
    public void registerClientManager() {
        if (clientConsumers != null) {
            ClientManager clientManager = new ClientManagerFabric();
            clientConsumers.forEach(consumer -> consumer.accept(clientManager));
        }
    }
}
