package com.almostreliable.almostlib.component;

import net.minecraft.world.item.ItemStack;

public interface ItemContainerAdapter extends Iterable<ItemStack> {

    boolean contains(ItemStack stack);

    boolean allowInsert();

    default ItemStack insert(ItemStack stack, boolean simulate) {
        return insert(stack, stack.getCount(), simulate);
    }

    ItemStack insert(ItemStack stack, int amount, boolean simulate);

    default ItemStack extract(ItemStack filter, boolean simulate) {
        return extract(filter, filter.getCount(), simulate);
    }

    ItemStack extract(ItemStack filter, int amount, boolean simulate);

    void clear();
}
