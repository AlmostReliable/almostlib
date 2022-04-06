package com.github.almostreliable.lib.registry;

import com.github.almostreliable.lib.AlmostConstants;
import com.github.almostreliable.lib.api.registry.RegistryEntry;
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
                    AlmostConstants.LOG.debug("Initialize forge registry {} for type {}",
                            forgeRegistry.getRegistryKey(),
                            forgeRegistry.getRegistrySuperType());
                    for (var entry : entries.entrySet()) {
                        RegistryEntryData<T> data = entry.getValue();
                        T createdValue = data.getFactory().get();
                        RegistryEntry<T> registryEntry = data.getRegistryEntry();
                        ResourceLocation id = registryEntry.getRegistryName();
                        registryEntry.updateReference(createdValue);
                        event.getRegistry().register(registryEntry.get().setRegistryName(id));
                        AlmostConstants.LOG.debug(" - Object '{}' got registered", registryEntry.getRegistryName());
                    }
                });
    }

}
