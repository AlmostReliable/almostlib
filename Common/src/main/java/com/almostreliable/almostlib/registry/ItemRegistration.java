package com.almostreliable.almostlib.registry;

import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;

import java.util.function.Supplier;

public class ItemRegistration extends Registration<Item, ItemEntry<? extends Item>> {
    ItemRegistration(String namespace, Registry<Item> registry) {
        super(namespace, registry);
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
}
