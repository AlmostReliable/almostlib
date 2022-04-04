package com.github.almostreliable.lib.api.registry;

import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;

public interface RegistryToRuleThemAll {
    String getNamespace();

    <T> IAlmostRegistry<T> getOrCreateRegistry(ResourceKey<Registry<T>> resourceKey);
}
