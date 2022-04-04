package com.github.almostreliable.lib.registry;

import com.github.almostreliable.lib.impl.registry.AbstractAlmostRegistry;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.ForgeRegistry;
import net.minecraftforge.registries.ForgeRegistryEntry;

import java.util.Objects;

public class ForgeAlmostRegistry<T extends ForgeRegistryEntry<T>> extends AbstractAlmostRegistry<T> {
    protected final ForgeRegistry<T> forgeRegistry;

    public ForgeAlmostRegistry(String namespace, ForgeRegistry<T> forgeRegistry) {
        super(namespace);
        Objects.requireNonNull(forgeRegistry);
        this.forgeRegistry = forgeRegistry;
    }

    @Override
    public void init() {
        FMLJavaModLoadingContext
                .get()
                .getModEventBus()
                .addGenericListener(forgeRegistry.getRegistrySuperType(), (RegistryEvent.Register<T> event) -> {
                    for (var entry : entries.entrySet()) {
                        event.getRegistry().register(entry.getValue().get().setRegistryName(entry.getKey()));
                    }
                });
    }
}
