package com.almostreliable.almostlib.fabric.component.compat;

import com.almostreliable.almostlib.component.EnergyContainer;
import com.google.common.base.Preconditions;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import team.reborn.energy.api.EnergyStorage;

@SuppressWarnings("UnstableApiUsage")
public class EnergyStorageWrapper implements EnergyContainer {

    protected final EnergyStorage storage;

    public EnergyStorageWrapper(EnergyStorage storage) {
        this.storage = storage;
    }

    @Override
    public boolean allowsInsertion() {
        return storage.supportsInsertion();
    }

    @Override
    public boolean allowsExtraction() {
        return storage.supportsExtraction();
    }

    @Override
    public long getAmount() {
        return storage.getAmount();
    }

    @Override
    public long getCapacity() {
        return storage.getCapacity();
    }

    @Override
    public long insert(long amount, boolean simulate) {
        Preconditions.checkArgument(amount > 0, "amount must be positive");

        try (Transaction tn = Transaction.openOuter()) {
            long inserted = storage.insert(amount, tn);
            if (simulate) {
                tn.abort();
            } else {
                tn.commit();
            }

            return inserted;
        }
    }

    @Override
    public long extract(long amount, boolean simulate) {
        Preconditions.checkArgument(amount > 0, "amount must be positive");

        try (Transaction tn = Transaction.openOuter()) {
            long extracted = storage.extract(amount, tn);
            if (simulate) {
                tn.abort();
            } else {
                tn.commit();
            }

            return extracted;
        }
    }

    @Override
    public void set(long amount) {
        Preconditions.checkArgument(amount >= 0, "amount must be non-negative");

        try (Transaction tn = Transaction.openOuter()) {
            storage.extract(getCapacity(), tn);
            storage.insert(amount, tn);
            tn.commit();
        }
    }
}
