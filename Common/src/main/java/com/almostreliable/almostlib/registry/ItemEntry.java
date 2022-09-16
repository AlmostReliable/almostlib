package com.almostreliable.almostlib.registry;

import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;

import javax.annotation.Nullable;

public interface ItemEntry<I extends Item> extends RegistryEntry<I>, ItemLike {

    @Override
    default I asItem() {
        return get();
    }

    default ItemStack asStack() {
        return new ItemStack(get());
    }

    default ItemStack asStack(int count) {
        return new ItemStack(get(), count);
    }

    @Nullable
    default Block asBlock() {
        if (get() instanceof BlockItem bi) {
            return bi.getBlock();
        }

        return null;
    }
}
