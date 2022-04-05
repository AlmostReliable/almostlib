package com.github.almostreliable.lib.registry;

import com.github.almostreliable.lib.api.registry.RegistryDelegate;

public class RegistryManagerFabric extends AbstractRegistryManager {
    public RegistryManagerFabric(String namespace) {
        super(namespace);
    }

    @Override
    public void init() {
        // TODO: Think about ordering
        for (RegistryDelegate<?> registry : registries) {
            registry.init();
        }
    }
}
