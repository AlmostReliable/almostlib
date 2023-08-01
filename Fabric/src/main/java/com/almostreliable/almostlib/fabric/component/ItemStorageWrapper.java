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

    protected final Storage<ItemVariant> delegate;

    public ItemStorageWrapper(Storage<ItemVariant> delegate) {
        this.delegate = delegate;
    }

    @Override
    public boolean allowsInsertion() {
        return delegate.supportsInsertion();
    }

    @Override
    public boolean allowsExtraction() {
        return delegate.supportsExtraction();
    }

    @Override
    public long insert(ItemStack filter, long amount, boolean simulate) {
        return handleInsert(delegate, filter, amount, simulate);
    }

    @Override
    public long extract(ItemStack filter, long amount, boolean simulate) {
        if (filter.isEmpty()) return 0;

        ItemVariant variant = ItemVariant.of(filter);
        if (variant.isBlank()) return 0;

        try (Transaction tn = Transaction.openOuter()) {
            long extracted = delegate.extract(variant, amount, tn);
            if (simulate) {
                tn.abort();
            } else {
                tn.commit();
            }

            return extracted;
        }
    }

    @Override
    public boolean contains(ItemStack filter) {
        if (filter.isEmpty()) return false;

        for (StorageView<ItemVariant> view : delegate) {
            if (view.getResource().matches(filter)) {
                return true;
            }
        }

        return false;
    }

    @Override
    public void clear() {
        try (var tn = Transaction.openOuter()) {
            for (StorageView<ItemVariant> view : delegate) {
                if (view.isResourceBlank()) continue;
                view.extract(view.getResource(), view.getAmount(), tn);
            }

            tn.commit();
        }
    }

    @Override
    public void clear(ItemStack filter) {
        if (filter.isEmpty()) return;

        ItemVariant variant = ItemVariant.of(filter);
        if (variant.isBlank()) return;

        try (var tn = Transaction.openOuter()) {
            for (StorageView<ItemVariant> view : delegate) {
                if (view.isResourceBlank() || !view.getResource().matches(filter)) {
                    continue;
                }
                view.extract(view.getResource(), view.getAmount(), tn);
            }

            tn.commit();
        }
    }

    @Override
    public Iterator<ItemView> iterator() {
        return new Iterator<>() {
            private final Iterator<StorageView<ItemVariant>> iterator = delegate.iterator();

            @Override
            public boolean hasNext() {
                return iterator.hasNext();
            }

            @Override
            public ItemView next() {
                return new FabricItemView(iterator.next());
            }
        };
    }

    private static long handleInsert(Storage<ItemVariant> delegate, ItemStack filter, long amount, boolean simulate) {
        if (filter.isEmpty()) return 0;
        ItemVariant variant = ItemVariant.of(filter);
        return handleInsert(delegate, variant, amount, simulate);
    }

    private static long handleInsert(Storage<ItemVariant> delegate, ItemVariant variant, long amount, boolean simulate) {
        if (variant.isBlank()) return 0;

        try (var tn = Transaction.openOuter()) {
            long inserted = delegate.insert(variant, amount, tn);
            if (simulate) {
                tn.abort();
            } else {
                tn.commit();
            }

            return inserted;
        }
    }

    private record FabricItemView(StorageView<ItemVariant> delegate) implements ItemView {

        @Override
        public Item getItem() {
            return delegate.getResource().getItem();
        }

        @Override
        public long getAmount() {
            return delegate.getAmount();
        }

        @Nullable
        @Override
        public CompoundTag getNbt() {
            return delegate.getResource().getNbt();
        }

        @Override
        public boolean allowsInsertion() {
            return delegate instanceof SingleSlotStorage<ItemVariant> storage && storage.supportsInsertion();
        }

        @Override
        public boolean allowsExtraction() {
            return delegate instanceof SingleSlotStorage<ItemVariant> storage && storage.supportsExtraction();
        }

        @Override
        public long insert(ItemStack filter, long amount, boolean simulate) {
            if (delegate instanceof SingleSlotStorage<ItemVariant> storage) {
                return handleInsert(storage, filter, amount, simulate);
            }
            return 0;
        }

        @Override
        public long insert(long amount, boolean simulate) {
            if (delegate instanceof SingleSlotStorage<ItemVariant> storage) {
                return handleInsert(storage, storage.getResource(), amount, simulate);
            }

            return 0;
        }

        @Override
        public long extract(long amount, boolean simulate) {
            if (delegate.isResourceBlank()) return 0;

            try (var tn = Transaction.openOuter()) {
                long extract = delegate.extract(delegate.getResource(), amount, tn);
                if (simulate) {
                    tn.abort();
                } else {
                    tn.commit();
                }

                return extract;
            }
        }

        @Override
        public void clear() {
            if (delegate.isResourceBlank()) return;

            try (var tn = Transaction.openOuter()) {
                delegate.extract(delegate.getResource(), delegate.getAmount(), tn);
                tn.commit();
            }
        }
    }
}
