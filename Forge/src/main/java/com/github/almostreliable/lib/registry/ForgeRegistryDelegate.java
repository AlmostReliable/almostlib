package com.github.almostreliable.lib.registry;

import com.github.almostreliable.lib.AlmostLib;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.IForgeRegistryEntry;

public class ForgeRegistryDelegate<T extends IForgeRegistryEntry<T>> extends RegistryDelegate<T> {
    private final IForgeRegistry<T> forgeRegistry;

    public ForgeRegistryDelegate(IForgeRegistry<T> forgeRegistry) {
        this.forgeRegistry = forgeRegistry;
    }

    @Override
    public void init() {
        FMLJavaModLoadingContext
                .get()
                .getModEventBus()
                .addGenericListener(forgeRegistry.getRegistrySuperType(), (RegistryEvent.Register<T> event) -> {
                    AlmostLib.LOG.debug("Initialize forge registry {} for type {}",
                            forgeRegistry.getRegistryKey(),
                            forgeRegistry.getRegistrySuperType());
                    for (var entry : entries.entrySet()) {
                        RegistryEntryData<T> data = entry.getValue();
                        T createdValue = data.getFactory().get();
                        RegistryEntry<T> registryEntry = data.getRegistryEntry();
                        ResourceLocation id = registryEntry.getRegistryName();
                        registryEntry.updateReference(createdValue);
                        event.getRegistry().register(registryEntry.get().setRegistryName(id));
                        AlmostLib.LOG.debug(" - Object '{}' got registered", registryEntry.getRegistryName());
                    }
                });
    }

    @Override
    public String getName() {
        return forgeRegistry.getRegistryName().toString();
    }

}
