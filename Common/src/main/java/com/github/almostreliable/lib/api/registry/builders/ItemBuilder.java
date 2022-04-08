package com.github.almostreliable.lib.api.registry.builders;

import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.level.ItemLike;

import java.util.function.Supplier;

public interface ItemBuilder<I extends Item> extends EntryBuilder<I, Item> {

    ItemBuilder<I> properties(Supplier<Item.Properties> supplier);

    ItemBuilder<I> food(FoodProperties food);

    ItemBuilder<I> maxStackSize(int size);

    ItemBuilder<I> defaultDurability(int durability);

    ItemBuilder<I> durability(int durability);

    ItemBuilder<I> craftRemainder(ItemLike itemLike);

    ItemBuilder<I> tab(CreativeModeTab tab);

    ItemBuilder<I> rarity(Rarity rarity);

    ItemBuilder<I> fireResistant();

}
