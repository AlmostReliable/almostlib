package com.github.almostreliable.lib.api.registry;

import com.github.almostreliable.lib.api.registry.builders.ItemBuilder;
import com.mojang.datafixers.util.Function4;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.Tier;

import java.util.function.Function;

public interface RegistryManager {
    String getNamespace();

    <I extends SwordItem> ItemBuilder<I> registerSword(String id, Tier tier, int atkDamage, int atkSpeed, Function4<Tier, Integer, Integer, Item.Properties, I> factory);

    <I extends Item> ItemBuilder<I> registerItem(String id, Function<Item.Properties, I> factory);

    void init();
}
