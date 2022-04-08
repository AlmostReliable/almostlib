package com.github.almostreliable.lib.registry;

import com.github.almostreliable.lib.registry.builders.RegistryEntryBuilder;

@FunctionalInterface
public interface RegisterCallback {
    <T, BASE> RegistryEntry<T> onRegister(RegistryEntryBuilder<T, BASE> builder);
}
