package com.almostreliable.almostlib.registry;

import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;

import java.util.function.Supplier;

public class BlockEntry<B extends Block> extends RegistryEntryImpl<B> implements ItemLike {
    public BlockEntry(Registry<B> registry, ResourceLocation id, Supplier<B> supplier) {
        super(registry, id, supplier);
    }

    @Override
    public Item asItem() {
        return get().asItem();
    }

    public ItemStack asStack() {
        return new ItemStack(get());
    }

    public ItemStack asStack(int count) {
        return new ItemStack(get(), count);
    }
}
