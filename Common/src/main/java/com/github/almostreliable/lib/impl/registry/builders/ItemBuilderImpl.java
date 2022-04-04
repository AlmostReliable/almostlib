package com.github.almostreliable.lib.impl.registry.builders;

import com.github.almostreliable.lib.api.registry.builders.ItemBuilder;
import com.github.almostreliable.lib.impl.registry.AbstractRegistryToRuleThemAll;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.level.ItemLike;

import java.util.Objects;
import java.util.function.Function;
import java.util.function.Supplier;

public class ItemBuilderImpl implements ItemBuilder {

    private final AbstractRegistryToRuleThemAll registry;
    private final Function<Item.Properties, ? extends Item> factory;
    private Item.Properties properties;

    public ItemBuilderImpl(AbstractRegistryToRuleThemAll registry, Function<Item.Properties, ? extends Item> factory) {
        this.registry = registry;
        this.factory = factory;
        this.properties = new Item.Properties();
    }

    @Override
    public ItemBuilder properties(Supplier<Item.Properties> supplier) {
        properties = supplier.get();
        Objects.requireNonNull(properties);
        return this;
    }

    @Override
    public ItemBuilder food(FoodProperties food) {
        properties.food(food);
        return this;
    }

    @Override
    public ItemBuilder maxStackSize(int size) {
        properties.stacksTo(size);
        return this;
    }

    @Override
    public ItemBuilder defaultDurability(int durability) {
        properties.defaultDurability(durability);
        return this;
    }

    @Override
    public ItemBuilder durability(int durability) {
        properties.durability(durability);
        return this;
    }

    @Override
    public ItemBuilder craftRemainder(ItemLike itemLike) {
        properties.craftRemainder(itemLike.asItem());
        return this;
    }

    @Override
    public ItemBuilder tab(CreativeModeTab tab) {
        properties.tab(tab);
        return this;
    }

    @Override
    public ItemBuilder rarity(Rarity rarity) {
        properties.rarity(rarity);
        return this;
    }

    @Override
    public ItemBuilder fireResistant() {
        properties.fireResistant();
        return this;
    }

    @Override
    public <T extends Item> Supplier<T> build() {
        // TODO change custom function
        //noinspection unchecked
        return () -> (T) factory.apply(properties);
    }
}
