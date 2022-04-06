package com.github.almostreliable.lib.registry.builders;

import com.github.almostreliable.lib.api.Utils;
import com.github.almostreliable.lib.api.registry.RegisterCallback;
import com.github.almostreliable.lib.api.registry.RegistryManager;
import com.github.almostreliable.lib.api.registry.builders.EntryBuilder;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;

import java.util.function.Supplier;

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

    public Supplier<T> register() {
        return registerCallback.onFinishRegister(id, this::create, getRegistryKey());
    }

    protected T getBuiltEntry() {
        Supplier<BASE> entry = manager.getEntry(this.getRegistryKey(), id);
        if (entry == null) {
            throw new IllegalStateException("Entry was not built at this moment or built incorrectly");
        }
        return Utils.cast(entry.get());
    }

    protected abstract ResourceKey<Registry<BASE>> getRegistryKey();
}
