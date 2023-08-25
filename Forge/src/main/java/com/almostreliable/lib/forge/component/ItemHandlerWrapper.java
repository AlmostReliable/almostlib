package com.almostreliable.lib.forge.component;

import com.almostreliable.lib.component.ItemContainer;
import com.almostreliable.lib.component.ItemView;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.IItemHandlerModifiable;

import javax.annotation.Nullable;
import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * A wrapper for an {@link IItemHandler} to expose it as an {@link ItemContainer}.
 * <p>
 * This is used to convert {@link IItemHandler}s for use with the component api.
 */
public class ItemHandlerWrapper implements ItemContainer {

    protected final IItemHandler itemHandler;

    public ItemHandlerWrapper(IItemHandler itemHandler) {
        this.itemHandler = itemHandler;
    }

    @Override
    public boolean allowsInsertion() {
        return true;
    }

    @Override
    public boolean allowsExtraction() {
        return true;
    }

    @Override
    public long insert(ItemStack filter, long amount, boolean simulate) {
        if (filter.isEmpty()) return 0;

        int toInsert = (int) Math.min(amount, Integer.MAX_VALUE);
        ItemStack remainder = filter.withCount(toInsert);

        for (int slot = 0; slot < size(); slot++) {
            remainder = itemHandler.insertItem(slot, remainder, simulate);

            if (remainder.isEmpty()) {
                return toInsert;
            }
        }

        return toInsert - remainder.getCount();
    }

    @Override
    public long extract(ItemStack filter, long amount, boolean simulate) {
        if (filter.isEmpty()) return 0;

        int toExtract = (int) Math.min(amount, Integer.MAX_VALUE);
        int extracted = 0;

        for (int slot = 0; slot < size(); slot++) {
            if (!itemHandler.getStackInSlot(slot).canStack(filter)) {
                continue;
            }

            ItemStack itemStack = itemHandler.extractItem(slot, toExtract - extracted, simulate);
            extracted += itemStack.getCount();

            if (extracted >= toExtract) {
                return extracted;
            }
        }

        return extracted;
    }

    @Override
    public boolean contains(ItemStack filter) {
        if (filter.isEmpty()) return false;

        for (int i = 0; i < size(); i++) {
            if (itemHandler.getStackInSlot(i).canStack(filter)) {
                return true;
            }
        }

        return false;
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
    public void clear(ItemStack filter) {
        if (filter.isEmpty()) return;

        if (itemHandler instanceof IItemHandlerModifiable m) {
            for (int i = 0; i < size(); i++) {
                if (!itemHandler.getStackInSlot(i).canStack(filter)) continue;
                m.setStackInSlot(i, ItemStack.EMPTY);
            }
            return;
        }

        for (int i = 0; i < size(); i++) {
            if (!itemHandler.getStackInSlot(i).canStack(filter)) continue;
            itemHandler.extractItem(i, Integer.MAX_VALUE, false);
        }
    }

    @Override
    public Iterator<ItemView> iterator() {
        return new Iterator<>() {

            private int index;

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

    public int size() {
        return itemHandler.getSlots();
    }

    private record ForgeItemView(IItemHandler itemHandler, int slot) implements ItemView {

        @Override
        public Item getItem() {
            return itemHandler.getStackInSlot(slot).getItem();
        }

        @Override
        public long getAmount() {
            return itemHandler.getStackInSlot(slot).getCount();
        }

        @Nullable
        @Override
        public CompoundTag getNbt() {
            return itemHandler.getStackInSlot(slot).getTag();
        }

        @Override
        public boolean allowsInsertion() {
            return true;
        }

        @Override
        public boolean allowsExtraction() {
            return true;
        }

        @Override
        public long insert(ItemStack filter, long amount, boolean simulate) {
            if (filter.isEmpty() || itemHandler.getStackInSlot(slot).isEmpty()) {
                return 0;
            }

            int toInsert = (int) Math.min(amount, filter.getMaxStackSize());
            ItemStack remainder = filter.withCount(toInsert);

            remainder = itemHandler.insertItem(slot, remainder, simulate);
            if (remainder.isEmpty()) {
                return toInsert;
            }

            return toInsert - remainder.getCount();
        }

        @Override
        public long insert(long amount, boolean simulate) {
            return insert(itemHandler.getStackInSlot(slot), amount, simulate);
        }

        @Override
        public long extract(long amount, boolean simulate) {
            if (itemHandler.getStackInSlot(slot).isEmpty()) {
                return 0;
            }

            int toExtract = (int) Math.min(amount, itemHandler.getStackInSlot(slot).getCount());
            ItemStack extracted = itemHandler.extractItem(slot, toExtract, simulate);

            return extracted.getCount();
        }

        @Override
        public void clear() {
            if (itemHandler instanceof IItemHandlerModifiable m) {
                m.setStackInSlot(slot, ItemStack.EMPTY);
                return;
            }

            itemHandler.extractItem(slot, Integer.MAX_VALUE, false);
        }
    }
}
