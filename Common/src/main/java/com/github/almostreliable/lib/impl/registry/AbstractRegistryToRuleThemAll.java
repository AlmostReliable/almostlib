package com.github.almostreliable.lib.impl.registry;

import com.github.almostreliable.lib.api.registry.IAlmostRegistry;
import com.github.almostreliable.lib.api.registry.RegistryToRuleThemAll;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;

public abstract class AbstractRegistryToRuleThemAll implements RegistryToRuleThemAll {

    private final String namespace;
    private final RegistryMap registries = new RegistryMap();

    public AbstractRegistryToRuleThemAll(String namespace) {
        this.namespace = namespace;
    }

    @Override
    public String getNamespace() {
        return this.namespace;
    }

    @Override
    public <T> IAlmostRegistry<T> getOrCreateRegistry(ResourceKey<Registry<T>> resourceKey) {
        return registries.getOrCreate(namespace, resourceKey);
    }
}
