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

/**
 * A wrapper for an {@link ItemVariant} {@link Storage} to expose it
 * as an {@link ItemContainer}.
 * <p>
 * This is used to convert {@link ItemVariant} {@link Storage}s for use
 * with the component api.
 */
@SuppressWarnings("UnstableApiUsage")
public class ItemStorageWrapper implements ItemContainer {

    protected final Storage<ItemVariant> itemStorage;

    public ItemStorageWrapper(Storage<ItemVariant> itemStorage) {
        this.itemStorage = itemStorage;
    }

    @Override
    public boolean allowsInsertion() {
        return itemStorage.supportsInsertion();
    }

    @Override
    public boolean allowsExtraction() {
        return itemStorage.supportsExtraction();
    }

    @Override
    public long insert(ItemStack filter, long amount, boolean simulate) {
        return handleInsert(itemStorage, filter, amount, simulate);
    }

    @Override
    public long extract(ItemStack filter, long amount, boolean simulate) {
        if (filter.isEmpty()) return 0;

        ItemVariant variant = ItemVariant.of(filter);
        if (variant.isBlank()) return 0;

        try (Transaction tn = Transaction.openOuter()) {
            long extracted = itemStorage.extract(variant, amount, tn);
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

        for (StorageView<ItemVariant> view : itemStorage) {
            if (view.getResource().matches(filter)) {
                return true;
            }
        }

        return false;
    }

    @Override
    public void clear() {
        try (var tn = Transaction.openOuter()) {
            for (StorageView<ItemVariant> view : itemStorage) {
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
            for (StorageView<ItemVariant> view : itemStorage) {
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
            private final Iterator<StorageView<ItemVariant>> iterator = itemStorage.iterator();

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

    private static long handleInsert(Storage<ItemVariant> itemStorage, ItemStack filter, long amount, boolean simulate) {
        if (filter.isEmpty()) return 0;
        ItemVariant variant = ItemVariant.of(filter);
        return handleInsert(itemStorage, variant, amount, simulate);
    }

    private static long handleInsert(Storage<ItemVariant> itemStorage, ItemVariant variant, long amount, boolean simulate) {
        if (variant.isBlank()) return 0;

        try (var tn = Transaction.openOuter()) {
            long inserted = itemStorage.insert(variant, amount, tn);
            if (simulate) {
                tn.abort();
            } else {
                tn.commit();
            }

            return inserted;
        }
    }

    private record FabricItemView(StorageView<ItemVariant> itemStorage) implements ItemView {

        @Override
        public Item getItem() {
            return itemStorage.getResource().getItem();
        }

        @Override
        public long getAmount() {
            return itemStorage.getAmount();
        }

        @Nullable
        @Override
        public CompoundTag getNbt() {
            return itemStorage.getResource().getNbt();
        }

        @Override
        public boolean allowsInsertion() {
            return itemStorage instanceof SingleSlotStorage<ItemVariant> storage && storage.supportsInsertion();
        }

        @Override
        public boolean allowsExtraction() {
            return itemStorage instanceof SingleSlotStorage<ItemVariant> storage && storage.supportsExtraction();
        }

        @Override
        public long insert(ItemStack filter, long amount, boolean simulate) {
            if (itemStorage instanceof SingleSlotStorage<ItemVariant> storage) {
                return handleInsert(storage, filter, amount, simulate);
            }
            return 0;
        }

        @Override
        public long insert(long amount, boolean simulate) {
            if (itemStorage instanceof SingleSlotStorage<ItemVariant> storage) {
                return handleInsert(storage, storage.getResource(), amount, simulate);
            }

            return 0;
        }

        @Override
        public long extract(long amount, boolean simulate) {
            if (itemStorage.isResourceBlank()) return 0;

            try (var tn = Transaction.openOuter()) {
                long extract = itemStorage.extract(itemStorage.getResource(), amount, tn);
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
            if (itemStorage.isResourceBlank()) return;

            try (var tn = Transaction.openOuter()) {
                itemStorage.extract(itemStorage.getResource(), itemStorage.getAmount(), tn);
                tn.commit();
            }
        }
    }
}
