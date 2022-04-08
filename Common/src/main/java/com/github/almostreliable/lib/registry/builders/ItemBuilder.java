package com.github.almostreliable.lib.registry.builders;

import com.github.almostreliable.lib.registry.RegisterCallback;
import com.github.almostreliable.lib.registry.RegistryManager;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.level.ItemLike;

import javax.annotation.Nullable;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.Supplier;

public class ItemBuilder<I extends Item> extends AbstractEntryBuilder<I, Item> {
    @Nullable
    private Function<Item.Properties, ? extends I> factory;
    private Item.Properties properties;

    public ItemBuilder(String id, Function<Item.Properties, ? extends I> factory, RegistryManager manager, RegisterCallback registerCallback) {
        this(id, manager, registerCallback);
        this.factory = factory;
    }

    ItemBuilder(String id, RegistryManager manager, RegisterCallback registerCallback) {
        super(id, registerCallback, manager);
        this.properties = new Item.Properties();
    }

    void setFactory(Function<Item.Properties, ? extends I> factory) {
        this.factory = factory;
    }

    public ItemBuilder<I> properties(Supplier<Item.Properties> supplier) {
        properties = supplier.get();

        Objects.requireNonNull(properties);
        return this;
    }

    public ItemBuilder<I> food(FoodProperties food) {
        properties.food(food);
        return this;
    }

    public ItemBuilder<I> maxStackSize(int size) {
        properties.stacksTo(size);
        return this;
    }

    public ItemBuilder<I> defaultDurability(int durability) {
        properties.defaultDurability(durability);
        return this;
    }

    public ItemBuilder<I> durability(int durability) {
        properties.durability(durability);
        return this;
    }

    public ItemBuilder<I> craftRemainder(ItemLike itemLike) {
        properties.craftRemainder(itemLike.asItem());
        return this;
    }

    public ItemBuilder<I> tab(CreativeModeTab tab) {
        properties.tab(tab);
        return this;
    }

    public ItemBuilder<I> rarity(Rarity rarity) {
        properties.rarity(rarity);
        return this;
    }

    public ItemBuilder<I> fireResistant() {
        properties.fireResistant();
        return this;
    }

    @Override
    public I create() {
        return factory.apply(properties);
    }

    @Override
    public ResourceKey<Registry<Item>> getRegistryKey() {
        return Registry.ITEM_REGISTRY;
    }
}
