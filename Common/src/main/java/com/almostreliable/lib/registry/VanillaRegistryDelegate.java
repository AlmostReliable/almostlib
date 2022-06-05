package com.almostreliable.lib.registry;

import com.almostreliable.lib.Utils;
import net.minecraft.core.Registry;

public class VanillaRegistryDelegate<T> extends RegistryDelegate<T> {
    private final Registry<T> registry;

    public VanillaRegistryDelegate(Registry<T> registry) {
        this.registry = registry;
    }

    @Override
    public void init() {
        // TODO Check how to set the logger for .debug in fabric
        Utils.LOG.debug("Initialize vanilla registry {}", registry);
        for (var entry : entries.entrySet()) {
            RegistryEntryData<T> data = entry.getValue();
            T createdValue = data.getFactory().get();
            RegistryEntry<T> registryEntry = data.getRegistryEntry();
            registryEntry.updateReference(createdValue);
            Registry.register(registry, registryEntry.getRegistryName(), registryEntry.get());
            Utils.LOG.debug(" - Object '{}' got registered", registryEntry.getRegistryName());
        }
    }

    @Override
    public String getName() {
        return registry.toString();
    }
}
