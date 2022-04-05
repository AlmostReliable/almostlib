package com.github.almostreliable.lib.api.registry;

import java.util.function.Supplier;

public interface RegistryDelegate<T> {
    <E extends T> Supplier<E> register(String namespace, String id, Supplier<? extends E> supplier);

    boolean isPresent();

    void init();
}
