package com.almostreliable.lib.registry;

import com.almostreliable.lib.registry.builders.RegistryEntryBuilder;

@FunctionalInterface
public interface RegisterCallback {
    <T, BASE> RegistryEntry<T> onRegister(RegistryEntryBuilder<T, BASE> builder);
}
