package com.almostreliable.almostlib.fabric.component;

import com.almostreliable.almostlib.component.ItemContainer;
import com.almostreliable.almostlib.component.ItemView;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.storage.StorageView;
import net.fabricmc.fabric.api.transfer.v1.storage.base.SingleSlotStorage;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import javax.annotation.Nullable;
import java.util.Iterator;

@SuppressWarnings("UnstableApiUsage")
public class ItemStorageWrapper implements ItemContainer {

    private final Storage<ItemVariant> storage;

    public ItemStorageWrapper(Storage<ItemVariant> storage) {
        this.storage = storage;
    }

    @Override
    public boolean contains(ItemStack stack) {
        for (StorageView<ItemVariant> view : storage) {
            if (view.getResource().matches(stack)) {
                return true;
            }
        }

        return false;
    }

    @Override
    public boolean allowInsert() {
        return storage.supportsInsertion();
    }

    @Override
    public ItemStack insert(ItemStack stack, int amount, boolean simulate) {
        amount = Math.min(amount, stack.getMaxStackSize());
        ItemVariant iv = ItemVariant.of(stack);
        long inserted = handleInsert(storage, iv, amount, simulate);
        return iv.toStack(amount - (int) inserted);
    }

    @Override
    public ItemStack extract(ItemStack filter, int amount, boolean simulate) {
        amount = Math.min(amount, filter.getMaxStackSize());

        try (Transaction tn = Transaction.openOuter()) {
            ItemVariant iv = ItemVariant.of(filter);
            long extract = storage.extract(iv, amount, tn);
            if (simulate) {
                tn.abort();
            } else {
                tn.commit();
            }

            return iv.toStack((int) extract);
        }
    }

    @Override
    public void clear() {
        try (var tn = Transaction.openOuter()) {
            for (StorageView<ItemVariant> view : storage) {
                if (!view.isResourceBlank()) {
                    view.extract(view.getResource(), view.getAmount(), tn);
                }
            }

            tn.commit();
        }
    }

    @Override
    public Iterator<ItemView> iterator() {
        return new Iterator<>() {
            final Iterator<StorageView<ItemVariant>> iterator = storage.iterator();

            @Override
            public boolean hasNext() {
                return iterator.hasNext();
            }

            @Override
            public ItemView next() {
                return new StorageItemView(iterator.next());
            }
        };
    }

    private record StorageItemView(StorageView<ItemVariant> view) implements ItemView {

        @Override
        public Item getItem() {
            return view.getResource().getItem();
        }

        @Override
        public long getAmount() {
            return view.getAmount();
        }

        @Nullable
        @Override
        public CompoundTag getNbt() {
            return view.getResource().getNbt();
        }

        @Override
        public void clear() {
            if (!view.isResourceBlank()) {
                try (var tn = Transaction.openOuter()) {
                    view.extract(view.getResource(), view.getAmount(), tn);
                    tn.commit();
                }
            }
        }

        @Override
        public boolean canInsert() {
            return view instanceof SingleSlotStorage<ItemVariant> storage && storage.supportsInsertion();
        }

        @Override
        public boolean canExtract() {
            return true;
        }

        @Override
        public long extract(long amount, boolean simulate) {
            try (var tn = Transaction.openOuter()) {
                long extract = view.extract(view.getResource(), amount, tn);
                if (simulate) {
                    tn.abort();
                } else {
                    tn.commit();
                }

                return extract;
            }
        }

        @Override
        public long insert(ItemStack item, long amount, boolean simulate) {
            if (view instanceof SingleSlotStorage<ItemVariant> storage) {
                ItemVariant variant = ItemVariant.of(item);
                return handleInsert(storage, variant, amount, simulate);
            }

            return amount;
        }

        @Override
        public long insert(long amount, boolean simulate) {
            if (view instanceof SingleSlotStorage<ItemVariant> storage) {
                return handleInsert(storage, storage.getResource(), amount, simulate);
            }

            return amount;
        }
    }

    public static long handleInsert(Storage<ItemVariant> storage, ItemVariant variant, long amount, boolean simulate) {
        if (variant.isBlank()) {
            return amount;
        }

        try (var tn = Transaction.openOuter()) {
            long inserted = storage.insert(variant, amount, tn);
            if (simulate) {
                tn.abort();
            } else {
                tn.commit();
            }

            return amount - inserted;
        }
    }
}
