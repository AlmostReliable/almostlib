package com.github.almostreliable.lib.impl.registry;

import com.github.almostreliable.lib.api.registry.RegistryToRuleThemAll;

public abstract class AbstractRegistryToRuleThemAll implements RegistryToRuleThemAll {

    private final String namespace;

    public AbstractRegistryToRuleThemAll(String namespace) {
        this.namespace = namespace;
    }

    @Override
    public String getNamespace() {
        return this.namespace;
    }
}
