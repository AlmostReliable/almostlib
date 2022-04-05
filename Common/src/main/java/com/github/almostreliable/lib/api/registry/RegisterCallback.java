package com.github.almostreliable.lib.api.registry;

import java.util.function.Supplier;

@FunctionalInterface
public interface RegisterCallback<T> {
    Supplier<T> onFinishRegister(String id, Supplier<T> entrySupplier);
}
