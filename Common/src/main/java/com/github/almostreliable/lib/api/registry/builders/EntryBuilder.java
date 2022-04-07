package com.github.almostreliable.lib.api.registry.builders;

import com.github.almostreliable.lib.api.registry.RegistryEntry;

public interface EntryBuilder<T> {
    RegistryEntry<T> register();
}
