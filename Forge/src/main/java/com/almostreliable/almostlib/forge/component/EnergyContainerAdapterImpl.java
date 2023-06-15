package com.almostreliable.almostlib.forge.component;

import com.almostreliable.almostlib.component.EnergyContainer;
import net.minecraftforge.energy.IEnergyStorage;

public class EnergyContainerAdapterImpl implements EnergyContainer {

    private final IEnergyStorage storage;

    public EnergyContainerAdapterImpl(IEnergyStorage storage) {
        this.storage = storage;
    }

    @Override
    public boolean canInsert() {
        return storage.canReceive();
    }

    @Override
    public boolean canExtract() {
        return storage.canExtract();
    }

    @Override
    public void set(long amount) {
        extract(getCapacity(), false);
        insert(amount, false);
    }

    @Override
    public long insert(long amount, boolean simulate) {
        return storage.receiveEnergy((int) amount, simulate);
    }

    @Override
    public long extract(long amount, boolean simulate) {
        return storage.extractEnergy((int) amount, simulate);
    }

    @Override
    public long getAmount() {
        return storage.getEnergyStored();
    }

    @Override
    public long getCapacity() {
        return storage.getMaxEnergyStored();
    }
}
