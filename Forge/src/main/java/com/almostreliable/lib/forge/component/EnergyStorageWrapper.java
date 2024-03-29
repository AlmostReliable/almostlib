package com.almostreliable.lib.forge.component;

import com.almostreliable.lib.component.EnergyContainer;
import com.google.common.base.Preconditions;
import net.minecraftforge.energy.IEnergyStorage;

/**
 * A wrapper for {@link IEnergyStorage} to expose it as an {@link EnergyContainer}.
 * <p>
 * This is used to convert {@link IEnergyStorage}s for use with the component api.
 */
public class EnergyStorageWrapper implements EnergyContainer {

    protected final IEnergyStorage storage;

    public EnergyStorageWrapper(IEnergyStorage storage) {
        this.storage = storage;
    }

    @Override
    public boolean allowsInsertion() {
        return storage.canReceive();
    }

    @Override
    public boolean allowsExtraction() {
        return storage.canExtract();
    }

    @Override
    public long getAmount() {
        return storage.getEnergyStored();
    }

    @Override
    public long getCapacity() {
        return storage.getMaxEnergyStored();
    }

    @Override
    public long insert(long amount, boolean simulate) {
        Preconditions.checkArgument(amount > 0, "amount must be positive");

        int toInsert = (int) Math.min(amount, Integer.MAX_VALUE);
        return storage.receiveEnergy(toInsert, simulate);
    }

    @Override
    public long extract(long amount, boolean simulate) {
        Preconditions.checkArgument(amount > 0, "amount must be positive");

        int toExtract = (int) Math.min(amount, Integer.MAX_VALUE);
        return storage.extractEnergy(toExtract, simulate);
    }

    @Override
    public void set(long amount) {
        Preconditions.checkArgument(amount >= 0, "amount must be non-negative");

        extract(getCapacity(), false);
        insert(amount, false);
    }
}
