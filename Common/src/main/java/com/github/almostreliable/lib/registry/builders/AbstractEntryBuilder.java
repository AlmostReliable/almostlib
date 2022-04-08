package com.github.almostreliable.lib.registry.builders;

import com.github.almostreliable.lib.api.registry.RegisterCallback;
import com.github.almostreliable.lib.api.registry.RegistryEntry;
import com.github.almostreliable.lib.api.registry.RegistryManager;
import com.github.almostreliable.lib.api.registry.builders.EntryBuilder;

public abstract class AbstractEntryBuilder<T, BASE> implements EntryBuilder<T, BASE> {
    protected final String name;
    protected final RegisterCallback registerCallback;
    protected final RegistryManager manager;

    public AbstractEntryBuilder(String name, RegisterCallback registerCallback, RegistryManager manager) {
        this.name = name;
        this.registerCallback = registerCallback;
        this.manager = manager;
    }

    public RegistryEntry<T> register() {
        return registerCallback.onRegister(this);
    }

    @Override
    public String getName() {
        return name;
    }
}
