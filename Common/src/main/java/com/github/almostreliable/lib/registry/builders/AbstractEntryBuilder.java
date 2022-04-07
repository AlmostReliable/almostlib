package com.github.almostreliable.lib.registry.builders;

import com.github.almostreliable.lib.api.registry.RegisterCallback;
import com.github.almostreliable.lib.api.registry.RegistryEntry;
import com.github.almostreliable.lib.api.registry.RegistryManager;
import com.github.almostreliable.lib.api.registry.builders.EntryBuilder;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;

public abstract class AbstractEntryBuilder<T, BASE> implements EntryBuilder<T> {
    protected final String id;
    protected final RegisterCallback registerCallback;
    protected final RegistryManager manager;

    public AbstractEntryBuilder(String id, RegisterCallback registerCallback, RegistryManager manager) {
        this.id = id;
        this.registerCallback = registerCallback;
        this.manager = manager;
    }

    public abstract T create();

    public RegistryEntry<T> register() {
        return registerCallback.onFinishRegister(id, this::create, getRegistryKey());
    }

    protected abstract ResourceKey<Registry<BASE>> getRegistryKey();
}
