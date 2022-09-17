package com.almostreliable.almostlib.registry;

import com.almostreliable.almostlib.mixin.ItemPropertiesAccessor;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.level.ItemLike;

import javax.annotation.Nullable;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.Supplier;

public class ItemRegistration extends Registration<Item, ItemEntry<? extends Item>> {

    @Nullable private CreativeModeTab creativeTab;

    ItemRegistration(String namespace, Registry<Item> registry) {
        super(namespace, registry);
    }

    public ItemRegistration defaultCreativeTab(CreativeModeTab creativeTab) {
        this.creativeTab = creativeTab;
        return this;
    }

    @Nullable
    public CreativeModeTab getDefaultCreativeTab() {
        return creativeTab;
    }

    @Override
    protected ItemEntry<? extends Item> createEntry(ResourceLocation id, Supplier<? extends Item> supplier) {
        return new ItemEntry<>() {
            @Override
            public ResourceLocation getId() {
                return id;
            }

            @Override
            public Item get() {
                return supplier.get();
            }
        };
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T extends Item> ItemEntry<T> register(String name, Supplier<? extends T> supplier) {
        return (ItemEntry<T>) super.register(name, supplier);
    }

    public <I extends Item> Builder<I> builder(String name, Function<Item.Properties, ? extends I> factory) {
        return new Builder<>(name, factory);
    }

    public Builder<Item> builder(String name) {
        return new Builder<>(name, Item::new);
    }

    public class Builder<I extends Item> {
        private final Function<Item.Properties, ? extends I> factory;
        private final String name;
        private Item.Properties properties;

        public Builder(String name, Function<Item.Properties, ? extends I> factory) {
            this.name = name;
            this.factory = factory;
            this.properties = new Item.Properties();
        }

        public Builder<I> properties(Supplier<Item.Properties> supplier) {
            properties = supplier.get();
            Objects.requireNonNull(properties);
            return this;
        }

        public Builder<I> food(FoodProperties food) {
            properties.food(food);
            return this;
        }

        public Builder<I> maxStackSize(int size) {
            properties.stacksTo(size);
            return this;
        }

        public Builder<I> defaultDurability(int durability) {
            properties.defaultDurability(durability);
            return this;
        }

        public Builder<I> durability(int durability) {
            properties.durability(durability);
            return this;
        }

        public Builder<I> craftRemainder(ItemLike itemLike) {
            properties.craftRemainder(itemLike.asItem());
            return this;
        }

        public Builder<I> creativeTab(CreativeModeTab tab) {
            properties.tab(tab);
            return this;
        }

        public Builder<I> rarity(Rarity rarity) {
            properties.rarity(rarity);
            return this;
        }

        public Builder<I> fireResistant() {
            properties.fireResistant();
            return this;
        }

        public ItemEntry<I> register() {
            final CreativeModeTab tab = ((ItemPropertiesAccessor) properties).getCreativeTab();
            final CreativeModeTab defaultTab = ItemRegistration.this.getDefaultCreativeTab();
            if (defaultTab != null && tab == null) {
                properties.tab(defaultTab);
            }
            return ItemRegistration.this.register(name, () -> factory.apply(properties));
        }
    }
}
