package com.almostreliable.almostlib.forge.component;

import com.almostreliable.almostlib.component.EnergyContainer;
import net.minecraftforge.energy.IEnergyStorage;

public class EnergyContainerWrapper implements IEnergyStorage {

    private final EnergyContainer component;

    public EnergyContainerWrapper(EnergyContainer component) {
        this.component = component;
    }

    @Override
    public int receiveEnergy(int amount, boolean simulate) {
        return (int) component.insert(amount, simulate);
    }

    @Override
    public int extractEnergy(int amount, boolean simulate) {
        return (int) component.extract(amount, simulate);
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
    public boolean canExtract() {
        return component.canExtract();
    }

    @Override
    public boolean canReceive() {
        return component.canInsert();
    }
}
