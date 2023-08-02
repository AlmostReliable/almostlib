package com.almostreliable.almostlib.forge.component;

import com.almostreliable.almostlib.component.EnergyContainer;
import com.google.common.base.Preconditions;
import net.minecraftforge.energy.IEnergyStorage;

public class EnergyContainerWrapper implements IEnergyStorage {

    protected final EnergyContainer component;

    public EnergyContainerWrapper(EnergyContainer component) {
        this.component = component;
    }

    @Override
    public boolean canReceive() {
        return component.allowsInsertion();
    }

    @Override
    public boolean canExtract() {
        return component.allowsExtraction();
    }

    @Override
    public int getEnergyStored() {
        return (int) component.getAmount();
    }

    @Override
    public int getMaxEnergyStored() {
        return (int) component.getCapacity();
    }

    @Override
    public int receiveEnergy(int amount, boolean simulate) {
        Preconditions.checkArgument(amount > 0, "amount must be positive");

        return (int) component.insert(amount, simulate);
    }

    @Override
    public int extractEnergy(int amount, boolean simulate) {
        Preconditions.checkArgument(amount > 0, "amount must be positive");

        return (int) component.extract(amount, simulate);
    }
}
