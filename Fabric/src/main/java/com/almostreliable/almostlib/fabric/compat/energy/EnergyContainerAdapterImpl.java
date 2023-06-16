package com.almostreliable.almostlib.fabric.compat.energy;

import com.almostreliable.almostlib.component.EnergyContainer;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import team.reborn.energy.api.EnergyStorage;

@SuppressWarnings("UnstableApiUsage")
public class EnergyContainerAdapterImpl implements EnergyContainer {

    private final EnergyStorage storage;

    public EnergyContainerAdapterImpl(EnergyStorage storage) {
        this.storage = storage;
    }

    @Override
    public boolean canInsert() {
        return storage.supportsInsertion();
    }

    @Override
    public boolean canExtract() {
        return storage.supportsExtraction();
    }

    @Override
    public void set(long amount) {
        try (Transaction tn = Transaction.openOuter()) {
            storage.extract(getCapacity(), tn);
            storage.insert(amount, tn);
            tn.commit();
        }
    }

    @Override
    public long insert(long amount, boolean simulate) {
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
    public long getAmount() {
        return storage.getAmount();
    }

    @Override
    public long getCapacity() {
        return storage.getCapacity();
    }
}
