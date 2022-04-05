package com.github.almostreliable.lib.registry.builders;

import com.github.almostreliable.lib.api.registry.builders.EntryBuilder;

import java.util.function.Supplier;

public abstract class AbstractEntryBuilder<T> implements EntryBuilder<T> {


    public AbstractEntryBuilder() {
    }

    public abstract T create();

    public Supplier<T> register() {
        // TODO Delegate to RTRTA
        return this::create;
    }
}
