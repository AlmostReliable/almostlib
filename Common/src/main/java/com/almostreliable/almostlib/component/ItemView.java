package com.almostreliable.almostlib.component;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import javax.annotation.Nullable;

public interface ItemView {

    Item getItem();

    long getAmount();

    @Nullable
    CompoundTag getNbt();

    void clear();

    boolean canInsert();

    boolean canExtract();

    long extract(long amount, boolean simulate);

    long insert(ItemStack item, long amount, boolean simulate);

    long insert(long amount, boolean simulate);
}
