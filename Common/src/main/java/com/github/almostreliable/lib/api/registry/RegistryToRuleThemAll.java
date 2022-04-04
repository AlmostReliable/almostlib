package com.github.almostreliable.lib.api.registry;

import com.github.almostreliable.lib.api.registry.builders.ItemBuilder;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Item;

import java.util.function.Function;

public interface RegistryToRuleThemAll {
    String getNamespace();

    <T> IAlmostRegistry<T> getOrCreateRegistry(ResourceKey<Registry<T>> resourceKey);

    ItemBuilder item(String id, Function<Item.Properties, Item> factory);
}
