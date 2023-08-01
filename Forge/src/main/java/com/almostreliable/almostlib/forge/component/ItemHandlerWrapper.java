package com.almostreliable.almostlib.forge.component;

import com.almostreliable.almostlib.component.ItemContainer;
import com.almostreliable.almostlib.component.ItemView;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.IItemHandlerModifiable;
import org.jetbrains.annotations.Nullable;

import java.util.Iterator;
import java.util.NoSuchElementException;

public class ItemHandlerWrapper implements ItemContainer {

    protected final IItemHandler delegate;

    public ItemHandlerWrapper(IItemHandler delegate) {
        this.delegate = delegate;
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
            remainder = delegate.insertItem(slot, remainder, simulate);

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
            if (!delegate.getStackInSlot(slot).canStack(filter)) {
                continue;
            }

            ItemStack itemStack = delegate.extractItem(slot, toExtract - extracted, simulate);
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
            if (delegate.getStackInSlot(i).canStack(filter)) {
                return true;
            }
        }

        return false;
    }

    @Override
    public void clear() {
        if (delegate instanceof IItemHandlerModifiable m) {
            for (int i = 0; i < size(); i++) {
                m.setStackInSlot(i, ItemStack.EMPTY);
            }
            return;
        }

        for (int i = 0; i < size(); i++) {
            delegate.extractItem(i, Integer.MAX_VALUE, false);
        }
    }

    @Override
    public void clear(ItemStack filter) {
        if (filter.isEmpty()) return;

        if (delegate instanceof IItemHandlerModifiable m) {
            for (int i = 0; i < size(); i++) {
                if (!delegate.getStackInSlot(i).canStack(filter)) continue;
                m.setStackInSlot(i, ItemStack.EMPTY);
            }
            return;
        }

        for (int i = 0; i < size(); i++) {
            if (!delegate.getStackInSlot(i).canStack(filter)) continue;
            delegate.extractItem(i, Integer.MAX_VALUE, false);
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

                ItemView view = new ForgeItemView(delegate, index);
                index++;
                return view;
            }
        };
    }

    public int size() {
        return delegate.getSlots();
    }

    private record ForgeItemView(IItemHandler delegate, int slot) implements ItemView {

        @Override
        public Item getItem() {
            return delegate.getStackInSlot(slot).getItem();
        }

        @Override
        public long getAmount() {
            return delegate.getStackInSlot(slot).getCount();
        }

        @Nullable
        @Override
        public CompoundTag getNbt() {
            return delegate.getStackInSlot(slot).getTag();
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
            if (filter.isEmpty() || delegate.getStackInSlot(slot).isEmpty()) {
                return 0;
            }

            int toInsert = (int) Math.min(amount, filter.getMaxStackSize());
            ItemStack remainder = filter.withCount(toInsert);

            remainder = delegate.insertItem(slot, remainder, simulate);
            if (remainder.isEmpty()) {
                return toInsert;
            }

            return toInsert - remainder.getCount();
        }

        @Override
        public long insert(long amount, boolean simulate) {
            return insert(delegate.getStackInSlot(slot), amount, simulate);
        }

        @Override
        public long extract(long amount, boolean simulate) {
            if (delegate.getStackInSlot(slot).isEmpty()) {
                return 0;
            }

            int toExtract = (int) Math.min(amount, delegate.getStackInSlot(slot).getCount());
            ItemStack extracted = delegate.extractItem(slot, toExtract, simulate);

            return extracted.getCount();
        }

        @Override
        public void clear() {
            if (delegate instanceof IItemHandlerModifiable m) {
                m.setStackInSlot(slot, ItemStack.EMPTY);
                return;
            }

            delegate.extractItem(slot, Integer.MAX_VALUE, false);
        }
    }
}
