package com.github.almostreliable.lib.api.registry;

import javax.annotation.Nullable;
import java.util.function.Supplier;

public interface RegistryDelegate<T> {
    <E extends T> Supplier<E> register(String id, Supplier<? extends E> supplier);

    boolean isPresent();

    void init(String namespace);

    @Nullable
    <E extends T> Supplier<E> find(String id);
}
