package com.github.almostreliable.lib.registry.builders;

import com.github.almostreliable.lib.api.registry.RegisterCallback;
import com.github.almostreliable.lib.api.registry.RegistryManager;
import com.github.almostreliable.lib.api.registry.builders.ItemBuilder;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.level.ItemLike;

import java.util.Objects;
import java.util.function.Function;
import java.util.function.Supplier;

public class ItemBuilderImpl<I extends Item> extends AbstractEntryBuilder<I, Item> implements ItemBuilder<I> {
    private final Function<Item.Properties, ? extends I> factory;
    private Item.Properties properties;

    public ItemBuilderImpl(String id, Function<Item.Properties, ? extends I> factory, RegistryManager manager, RegisterCallback registerCallback) {
        super(id, registerCallback, manager);
        this.factory = factory;
        this.properties = new Item.Properties();
    }

    @Override
    public ItemBuilder<I> properties(Supplier<Item.Properties> supplier) {
        properties = supplier.get();
        Objects.requireNonNull(properties);
        return this;
    }

    @Override
    public ItemBuilder<I> food(FoodProperties food) {
        properties.food(food);
        return this;
    }

    @Override
    public ItemBuilder<I> maxStackSize(int size) {
        properties.stacksTo(size);
        return this;
    }

    @Override
    public ItemBuilder<I> defaultDurability(int durability) {
        properties.defaultDurability(durability);
        return this;
    }

    @Override
    public ItemBuilder<I> durability(int durability) {
        properties.durability(durability);
        return this;
    }

    @Override
    public ItemBuilder<I> craftRemainder(ItemLike itemLike) {
        properties.craftRemainder(itemLike.asItem());
        return this;
    }

    @Override
    public ItemBuilder<I> tab(CreativeModeTab tab) {
        properties.tab(tab);
        return this;
    }

    @Override
    public ItemBuilder<I> rarity(Rarity rarity) {
        properties.rarity(rarity);
        return this;
    }

    @Override
    public ItemBuilder<I> fireResistant() {
        properties.fireResistant();
        return this;
    }

    @Override
    public I create() {
        return factory.apply(properties);
    }

    @Override
    protected ResourceKey<Registry<Item>> getRegistryKey() {
        return Registry.ITEM_REGISTRY;
    }
}
