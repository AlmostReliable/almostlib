package com.github.almostreliable.lib.registry;

import com.github.almostreliable.lib.api.registry.RegistryDelegate;

import java.util.function.Supplier;

public abstract class AbstractRegistryDelegate<T> implements RegistryDelegate<T> {

    private final Supplier<String> namespace;

    public AbstractRegistryDelegate(Supplier<String> namespace) {
        this.namespace = namespace;
    }

    @Override
    public String getNamespace() {
        return namespace.get();
    }
}
