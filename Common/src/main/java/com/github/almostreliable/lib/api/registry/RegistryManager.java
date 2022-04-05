package com.github.almostreliable.lib.api.registry;

import com.github.almostreliable.lib.api.registry.builders.ItemBuilder;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Item;

import java.util.function.Function;

public interface RegistryManager {
    String getNamespace();

    <I extends Item> ItemBuilder<I> item(String id, Function<Item.Properties, I> factory);

    void init();
}
