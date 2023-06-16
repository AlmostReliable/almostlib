package com.almostreliable.almostlib.fabric.component;

import com.almostreliable.almostlib.component.ItemContainerAdapter;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.storage.StorageView;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.minecraft.world.item.ItemStack;

import java.util.Iterator;

@SuppressWarnings("UnstableApiUsage")
public class ItemContainerAdapterImpl implements ItemContainerAdapter {

    private final Storage<ItemVariant> storage;

    public ItemContainerAdapterImpl(Storage<ItemVariant> storage) {
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

        try (Transaction tn = Transaction.openOuter()) {
            ItemVariant iv = ItemVariant.of(stack);
            long inserted = storage.insert(iv, amount, tn);
            if (simulate) {
                tn.abort();
            } else {
                tn.commit();
            }

            return iv.toStack(amount - (int) inserted);
        }
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
    public Iterator<ItemStack> iterator() {
        return new Iterator<>() {
            final Iterator<StorageView<ItemVariant>> iterator = storage.iterator();

            @Override
            public boolean hasNext() {
                return iterator.hasNext();
            }

            @Override
            public ItemStack next() {
                return iterator.next().getResource().toStack();
            }
        };
    }
}
