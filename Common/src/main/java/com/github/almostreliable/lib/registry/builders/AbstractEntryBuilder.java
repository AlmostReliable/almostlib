package com.github.almostreliable.lib.registry.builders;

import com.github.almostreliable.lib.api.registry.RegisterCallback;
import com.github.almostreliable.lib.api.registry.builders.EntryBuilder;

import java.util.function.Supplier;

public abstract class AbstractEntryBuilder<T> implements EntryBuilder<T> {
    protected final String id;
    private final RegisterCallback<T> registerCallback;

    public AbstractEntryBuilder(String id, RegisterCallback<T> registerCallback) {
        this.id = id;
        this.registerCallback = registerCallback;
    }

    public abstract T create();

    public Supplier<T> register() {
        return registerCallback.onFinishRegister(id, this::create);
    }
}
