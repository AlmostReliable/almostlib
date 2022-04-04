package com.github.almostreliable.lib.api.registry;

import java.util.function.Supplier;

public interface IAlmostRegistry<T> {
    <E extends T> Supplier<E> register(String id, Supplier<E> supplier);

    void init();
}
