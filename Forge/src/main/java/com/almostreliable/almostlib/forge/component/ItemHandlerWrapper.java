package com.almostreliable.almostlib.forge.component;

import com.almostreliable.almostlib.component.ItemContainer;
import com.almostreliable.almostlib.component.ItemView;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.ItemHandlerHelper;
import org.jetbrains.annotations.Nullable;

import java.util.Iterator;
import java.util.NoSuchElementException;

public class ItemHandlerWrapper implements ItemContainer {

    protected final IItemHandler itemHandler;

    public ItemHandlerWrapper(IItemHandler itemHandler) {
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

        ItemStack remainder = filter.copy();
        remainder.setCount(toInsert);

        for (int slot = 0; slot < size(); slot++) {
            remainder = itemHandler.insertItem(slot, remainder, simulate);

            if (remainder.isEmpty()) {
                return ItemStack.EMPTY;
            }
        }

        return remainder;
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
    public void clear() {
        if (itemHandler instanceof IItemHandlerModifiable m) {
            for (int i = 0; i < size(); i++) {
                m.setStackInSlot(i, ItemStack.EMPTY);
            }

            return;
        }

        for (int i = 0; i < size(); i++) {
            itemHandler.extractItem(i, Integer.MAX_VALUE, false);
        }
    }

    @Override
    public Iterator<ItemView> iterator() {
        return new Iterator<>() {
            int index = 0;

            @Override
            public boolean hasNext() {
                return index < size();
            }

            @Override
            public ItemView next() {
                if (!hasNext()) {
                    throw new NoSuchElementException();
                }

                ItemView view = new ForgeItemView(itemHandler, index);
                index++;
                return view;
            }
        };
    }

    private record ForgeItemView(IItemHandler handler, int slot) implements ItemView {

        @Override
        public Item getItem() {
            return handler.getStackInSlot(slot).getItem();
        }

        @Override
        public long getAmount() {
            return handler.getStackInSlot(slot).getCount();
        }

        @Nullable
        @Override
        public CompoundTag getNbt() {
            return handler.getStackInSlot(slot).getTag();
        }

        @Override
        public void clear() {
            if (handler instanceof IItemHandlerModifiable m) {
                m.setStackInSlot(slot, ItemStack.EMPTY);
            } else {
                handler.extractItem(slot, Integer.MAX_VALUE, false);
            }
        }

        @Override
        public boolean canInsert() {
            return true;
        }

        @Override
        public boolean canExtract() {
            return true;
        }

        @Override
        public long extract(long amount, boolean simulate) {
            ItemStack extractItem = handler.extractItem(slot, (int) amount, simulate);
            if (extractItem.isEmpty()) {
                return 0;
            }

            return extractItem.getCount();
        }

        @Override
        public long insert(ItemStack item, long amount, boolean simulate) {
            ItemStack itemStack = handler.insertItem(slot, item, simulate);
            if (itemStack.isEmpty()) {
                return 0;
            }

            return itemStack.getCount();
        }

        @Override
        public long insert(long amount, boolean simulate) {
            return insert(handler.getStackInSlot(slot), amount, simulate);
        }
    }
}
