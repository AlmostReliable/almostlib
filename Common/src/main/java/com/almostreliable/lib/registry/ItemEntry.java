package com.almostreliable.lib.registry;

import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;

import javax.annotation.Nullable;
import java.util.function.Supplier;

public class ItemEntry<I extends Item> extends RegistryEntryImpl<I> implements ItemLike {

    public ItemEntry(Registry<I> registry, ResourceLocation id, Supplier<I> supplier) {
        super(registry, id, supplier);
    }

    @Override
    public I asItem() {
        return get();
    }

    public ItemStack asStack() {
        return new ItemStack(get());
    }

    public ItemStack asStack(int count) {
        return new ItemStack(get(), count);
    }

    @Nullable
    public Block asBlock() {
        if (get() instanceof BlockItem bi) {
            return bi.getBlock();
        }

        return null;
    }
}
