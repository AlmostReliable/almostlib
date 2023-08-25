package com.almostreliable.lib.forge.component;

import com.almostreliable.lib.component.EnergyContainer;
import com.google.common.base.Preconditions;
import net.minecraftforge.energy.IEnergyStorage;

/**
 * A wrapper for {@link EnergyContainer} to expose it as an {@link IEnergyStorage}.
 * <p>
 * Remember that you can work with an {@link EnergyContainer} directly if you want to.<br>
 * The wrapper is only required for internal use and should only be used if you need to pass
 * an {@link IEnergyStorage} to a method that requires it.
 */
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
