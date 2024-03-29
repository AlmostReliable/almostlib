package com.almostreliable.lib.fabric.component.compat;

import com.almostreliable.lib.component.EnergyContainer;
import com.google.common.base.Preconditions;
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;
import net.fabricmc.fabric.api.transfer.v1.transaction.base.SnapshotParticipant;
import team.reborn.energy.api.EnergyStorage;

/**
 * A wrapper for {@link EnergyContainer} to expose it as an {@link EnergyStorage}.
 * <p>
 * Remember that you can work with an {@link EnergyContainer} directly if you want to.<br>
 * The wrapper is only required for internal use and should only be used if you need to pass
 * an {@link EnergyStorage} to a method that requires it.
 */
@SuppressWarnings("UnstableApiUsage")
public class EnergyContainerWrapper extends SnapshotParticipant<Long> implements EnergyStorage {

    protected final EnergyContainer component;

    public EnergyContainerWrapper(EnergyContainer component) {
        this.component = component;
    }

    @Override
    public boolean supportsInsertion() {
        return component.allowsInsertion();
    }

    @Override
    public boolean supportsExtraction() {
        return component.allowsExtraction();
    }

    @Override
    public long getAmount() {
        return component.getAmount();
    }

    @Override
    public long getCapacity() {
        return component.getCapacity();
    }

    @Override
    public long insert(long amount, TransactionContext transaction) {
        Preconditions.checkArgument(amount > 0, "amount must be positive");

        if (component.insert(amount, true) > 0) {
            updateSnapshots(transaction);
            return component.insert(amount, false);
        }

        return 0;
    }

    @Override
    public long extract(long amount, TransactionContext transaction) {
        Preconditions.checkArgument(amount > 0, "amount must be positive");

        if (component.extract(amount, true) > 0) {
            updateSnapshots(transaction);
            return component.extract(amount, false);
        }

        return 0;
    }

    @Override
    protected Long createSnapshot() {
        return component.getAmount();
    }

    @Override
    protected void readSnapshot(Long snapshot) {
        component.set(snapshot);
    }
}
