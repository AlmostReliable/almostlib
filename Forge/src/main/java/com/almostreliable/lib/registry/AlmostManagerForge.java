package com.almostreliable.lib.registry;

import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.ForgeRegistry;

import java.util.Objects;

public class AlmostManagerForge extends AlmostManager {
    public AlmostManagerForge(String namespace) {
        super(namespace);
    }

    @Override
    @SuppressWarnings({ "unchecked", "rawtypes" })
    protected <T> RegistryDelegate<T> getOrCreateDelegate(ResourceKey<Registry<T>> resourceKey) {
        return (RegistryDelegate<T>) registries.computeIfAbsent(resourceKey, key -> {
            ForgeRegistry registry = net.minecraftforge.registries.RegistryManager.ACTIVE.getRegistry(key.location());
            if (registry == null) {
                Registry<T> vanillaRegistry = (Registry<T>) Registry.REGISTRY.get(key.location());
                Objects.requireNonNull(vanillaRegistry, "Something went wrong"); // TODO handle this?
                return new VanillaRegistryDelegate<>(vanillaRegistry);
            }
            return (RegistryDelegate<T>) new ForgeRegistryDelegate<>(registry);
        });
    }

    @Override
    public void registerClientManager() {
        var eventBus = FMLJavaModLoadingContext.get().getModEventBus();
        eventBus.addListener((FMLClientSetupEvent event) -> {
            if (clientConsumers != null) {
                ClientManager clientManager = new ClientManagerForge();
                clientConsumers.forEach(consumer -> consumer.accept(clientManager));
            }
        });
    }
}
