package com.github.almostreliable.lib.registry;

import com.github.almostreliable.lib.AlmostConstants;
import com.github.almostreliable.lib.api.registry.RegistryEntry;
import net.minecraft.core.Registry;

public class VanillaRegistryDelegate<T> extends RegistryDelegate<T> {
    private final Registry<T> registry;

    public VanillaRegistryDelegate(Registry<T> registry) {
        this.registry = registry;
    }

    @Override
    public void init() {
        // TODO Check how to set the logger for .debug in fabric
        AlmostConstants.LOG.debug("Initialize vanilla registry {}", registry);
        for (var entry : entries.entrySet()) {
            RegistryEntryData<T> data = entry.getValue();
            T createdValue = data.getFactory().get();
            RegistryEntry<T> registryEntry = data.getRegistryEntry();
            registryEntry.updateReference(createdValue);
            Registry.register(registry, registryEntry.getRegistryName(), registryEntry.get());
            AlmostConstants.LOG.debug(" - Object '{}' got registered", registryEntry.getRegistryName());
        }
    }
}
