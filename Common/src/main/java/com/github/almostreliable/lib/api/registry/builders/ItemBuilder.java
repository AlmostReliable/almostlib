package com.github.almostreliable.lib.api.registry.builders;

import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.level.ItemLike;

import java.util.function.Supplier;

public interface ItemBuilder {

    ItemBuilder properties(Supplier<Item.Properties> supplier);

    ItemBuilder food(FoodProperties food);

    ItemBuilder maxStackSize(int size);

    ItemBuilder defaultDurability(int durability);

    ItemBuilder durability(int durability);

    ItemBuilder craftRemainder(ItemLike itemLike);

    ItemBuilder tab(CreativeModeTab tab);

    ItemBuilder rarity(Rarity rarity);

    ItemBuilder fireResistant();

    <T extends Item> Supplier<T> build();
}
