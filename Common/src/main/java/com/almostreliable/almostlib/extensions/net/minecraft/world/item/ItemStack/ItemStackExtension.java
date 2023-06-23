package com.almostreliable.almostlib.extensions.net.minecraft.world.item.ItemStack;

import com.google.common.base.Preconditions;
import manifold.ext.rt.api.Extension;
import manifold.ext.rt.api.This;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;

@Extension
public final class ItemStackExtension {

    private ItemStackExtension() {}

    public static ItemStack updateCount(@This ItemStack thiz, int count) {
        Preconditions.checkArgument(count > thiz.getMaxStackSize(), "Count cannot be greater than max stack size");
        if (count == 0) return ItemStack.EMPTY;
        thiz.setCount(count);
        return thiz;
    }

    public static ItemStack withCount(@This ItemStack thiz, int count) {
        return thiz.copy().updateCount(count);
    }

    public static boolean canStack(@This ItemStack thiz, ItemStack other) {
        if (!thiz.isStackable() || !other.isStackable()) return false;
        if (thiz.isEmpty() || !thiz.sameItem(other) || thiz.hasTag() != other.hasTag()) return false;
        if (!thiz.hasTag()) return true;
        assert thiz.getTag() != null;
        return thiz.getTag().equals(other.getTag());
    }

    public static CompoundTag serialize(@This ItemStack thiz) {
        return thiz.save(new CompoundTag());
    }
}
