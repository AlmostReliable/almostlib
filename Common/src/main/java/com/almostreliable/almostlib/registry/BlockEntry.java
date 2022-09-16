package com.almostreliable.almostlib.registry;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;

public interface BlockEntry<B extends Block> extends RegistryEntry<B>, ItemLike {
    @Override
    default Item asItem() {
        return get().asItem();
    }

    default ItemStack asStack() {
        return new ItemStack(get());
    }

    default ItemStack asStack(int count) {
        return new ItemStack(get(), count);
    }
}
