package com.github.almostreliable.lib.registry;

import com.github.almostreliable.lib.api.registry.RegistryDelegate;

public class RegistryManagerForge extends AbstractRegistryManager {
    public RegistryManagerForge(String namespace) {
        super(namespace);
    }

    @Override
    public void init() {
        for (RegistryDelegate<?> registry : registries) {
            registry.init();
        }
    }
}
