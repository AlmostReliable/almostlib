package com.github.almostreliable.lib.api.registry.builders;

import java.util.function.Supplier;

public interface EntryBuilder<T> {
    Supplier<T> register();
}
