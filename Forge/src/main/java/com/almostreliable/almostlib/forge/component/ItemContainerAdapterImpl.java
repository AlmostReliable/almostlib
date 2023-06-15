package com.almostreliable.almostlib.forge.component;

import com.almostreliable.almostlib.component.ItemContainerAdapter;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemHandlerHelper;

import java.util.Iterator;

public class ItemContainerAdapterImpl implements ItemContainerAdapter {

    protected final IItemHandler itemHandler;

    public ItemContainerAdapterImpl(IItemHandler itemHandler) {
        this.itemHandler = itemHandler;
    }

    public int size() {
        return itemHandler.getSlots();
    }

    @Override
    public boolean contains(ItemStack stack) {
        for (int i = 0; i < size(); i++) {
            if (ItemHandlerHelper.canItemStacksStack(stack, itemHandler.getStackInSlot(i))) {
                return true;
            }
        }

        return false;
    }

    @Override
    public boolean allowInsert() {
        return size() > 0;
    }

    @Override
    public ItemStack insert(ItemStack filter, int amount, boolean simulate) {
        int toInsert = Math.min(amount, filter.getMaxStackSize());
        ItemStack result = ItemStack.EMPTY;

        for (int slot = 0; slot < size(); slot++) {
            if (toInsert <= 0) {
                break;
            }

            ItemStack itemToInsert = filter.copy();
            itemToInsert.setCount(toInsert);
            ItemStack remainder = itemHandler.insertItem(slot, itemToInsert, simulate);
            if (remainder.isEmpty()) {
                return ItemStack.EMPTY;
            }

            toInsert -= remainder.getCount();
            result = remainder;
        }

        return result;
    }

    @Override
    public ItemStack extract(ItemStack filter, int amount, boolean simulate) {
        amount = Math.min(amount, filter.getMaxStackSize());
        int extractedAmount = 0;
        ItemStack result = ItemStack.EMPTY;

        for (int slot = 0; slot < size(); slot++) {
            if (extractedAmount >= amount) {
                break;
            }

            ItemStack stackInSlot = itemHandler.getStackInSlot(slot);
            if (!ItemHandlerHelper.canItemStacksStack(filter, stackInSlot)) {
                continue;
            }

            ItemStack itemStack = itemHandler.extractItem(slot, amount - extractedAmount, simulate);
            if (!itemStack.isEmpty()) {
                extractedAmount += itemStack.getCount();
                result = itemStack;
            }
        }

        if (!result.isEmpty()) {
            result.setCount(extractedAmount);
        }

        return result;
    }

    @Override
    public Iterator<ItemStack> iterator() {
        return new Iterator<>() {
            int index = 0;

            @Override
            public boolean hasNext() {
                return index < size();
            }

            @Override
            public ItemStack next() {
                ItemStack next = itemHandler.getStackInSlot(index);
                index++;
                return next;
            }
        };
    }
}
