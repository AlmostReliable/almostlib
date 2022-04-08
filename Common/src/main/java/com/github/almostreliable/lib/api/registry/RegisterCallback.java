package com.github.almostreliable.lib.api.registry;

import com.github.almostreliable.lib.api.registry.builders.EntryBuilder;

@FunctionalInterface
public interface RegisterCallback {
    <T, BASE> RegistryEntry<T> onRegister(EntryBuilder<T, BASE> builder);
}
