package com.github.almostreliable.lib.registry.builders;

import com.github.almostreliable.lib.registry.RegisterCallback;
import com.github.almostreliable.lib.registry.RegistryEntry;
import com.github.almostreliable.lib.registry.RegistryManager;

public abstract class AbstractEntryBuilder<T, BASE> implements RegistryEntryBuilder<T, BASE> {
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
