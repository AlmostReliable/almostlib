package com.github.almostreliable.lib.registry;

import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.IForgeRegistryEntry;

public class ForgeRegistryDelegate<T extends IForgeRegistryEntry<T>> extends AbstractRegistryDelegate<T> {
    private final IForgeRegistry<T> forgeRegistry;

    public ForgeRegistryDelegate(IForgeRegistry<T> forgeRegistry) {
        this.forgeRegistry = forgeRegistry;
    }

    @Override
    public void init(String namespace) {
        FMLJavaModLoadingContext
                .get()
                .getModEventBus()
                .addGenericListener(forgeRegistry.getRegistrySuperType(), (RegistryEvent.Register<T> event) -> {
                    for (var entry : entries.entrySet()) {
                        ResourceLocation location = new ResourceLocation(namespace, entry.getKey());
                        event.getRegistry().register(entry.getValue().get().setRegistryName(location));
                    }
                });
    }

}
