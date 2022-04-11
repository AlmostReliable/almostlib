package com.github.almostreliable.lib.registry.builders;

import com.github.almostreliable.lib.registry.RegistryEntry;

@FunctionalInterface
public interface PostRegisterListener<T> {
    void onPostRegister(RegistryEntry<T> registryEntry);
}
