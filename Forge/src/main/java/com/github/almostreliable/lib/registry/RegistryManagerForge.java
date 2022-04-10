package com.github.almostreliable.lib.registry;

import com.github.almostreliable.lib.Utils;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.ForgeRegistry;

import java.util.Objects;

public class RegistryManagerForge extends RegistryManager {
    public RegistryManagerForge(String namespace) {
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
    public void initClient() {
        var eventBus = FMLJavaModLoadingContext.get().getModEventBus();
        eventBus.addListener(this::handleBlockEntityRenderers);
    }

    private void handleBlockEntityRenderers(EntityRenderersEvent.RegisterRenderers event) {
        for (var entry : blockEntityRenderers.entrySet()) {
            event.registerBlockEntityRenderer(entry.getKey().get(), Utils.cast(entry.getValue()));
        }
    }
}
