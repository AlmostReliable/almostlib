package com.github.almostreliable.lib.registry;

import com.github.almostreliable.lib.Utils;
import net.fabricmc.fabric.api.client.rendering.v1.BlockEntityRendererRegistry;
import net.minecraft.client.gui.screens.MenuScreens;
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
        registeredBlockEntityRendererFactories.forEach((registryEntry, provider) -> {
            BlockEntityRendererRegistry.register(Utils.cast(registryEntry.get()), provider.get()::apply);
        });

        registeredScreenFactories.forEach((registryEntry, screenFactory) -> {
            MenuScreens.register(Utils.cast(registryEntry.get()), screenFactory.get()::create);
        });
    }

}
