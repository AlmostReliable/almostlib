package com.almostreliable.lib.component.impl;

import com.almostreliable.lib.component.EnergyContainer;
import com.almostreliable.lib.util.Serializable;
import net.minecraft.nbt.CompoundTag;

import javax.annotation.Nullable;

/**
 * A simple implementation of {@link EnergyContainer}.
 * <p>
 * This implementation is serializable.
 */
public class SimpleEnergyContainer implements EnergyContainer, Serializable<CompoundTag> {

    @Nullable private final Runnable changeListener;

    protected long capacity;
    protected long maxInsert;
    protected long maxExtract;
    protected long energy;

    public SimpleEnergyContainer(long capacity, long maxInsert, long maxExtract, long energy, @Nullable Runnable changeListener) {
        this.capacity = capacity;
        this.maxInsert = maxInsert;
        this.maxExtract = maxExtract;
        this.energy = energy;
        this.changeListener = changeListener;
    }

    public SimpleEnergyContainer(long capacity, long maxInsert, long maxExtract, long energy) {
        this(capacity, maxInsert, maxExtract, energy, null);
    }

    public SimpleEnergyContainer(long capacity, long maxInsert, long maxExtract, @Nullable Runnable changeListener) {
        this(capacity, maxInsert, maxExtract, 0, changeListener);
    }

    public SimpleEnergyContainer(long capacity, long maxInsert, long maxExtract) {
        this(capacity, maxInsert, maxExtract, 0);
    }

    public SimpleEnergyContainer(long capacity, long maxTransfer, @Nullable Runnable changeListener) {
        this(capacity, maxTransfer, maxTransfer, changeListener);
    }

    public SimpleEnergyContainer(long capacity, long maxTransfer) {
        this(capacity, maxTransfer, maxTransfer);
    }

    public SimpleEnergyContainer(long capacity, @Nullable Runnable changeListener) {
        this(capacity, capacity, changeListener);
    }

    public SimpleEnergyContainer(long capacity) {
        this(capacity, capacity);
    }

    @Override
    public boolean allowsInsertion() {
        return maxInsert > 0;
    }

    @Override
    public boolean allowsExtraction() {
        return maxExtract > 0;
    }

    @Override
    public long getAmount() {
        return energy;
    }

    @Override
    public long getCapacity() {
        return capacity;
    }

    @Override
    public long insert(long amount, boolean simulate) {
        if (!allowsInsertion()) {
            return 0;
        }

        long toInsert = Math.min(amount, Math.min(capacity - energy, maxInsert));
        if (!simulate) {
            energy += toInsert;
            onChange();
        }

        return toInsert;
    }

    @Override
    public long extract(long amount, boolean simulate) {
        if (!allowsExtraction()) {
            return 0;
        }

        long toExtract = Math.min(amount, Math.min(energy, maxExtract));
        if (!simulate) {
            energy -= toExtract;
            onChange();
        }

        return toExtract;
    }

    @Override
    public void set(long amount) {
        energy = amount;
        onChange();
    }

    @Override
    public CompoundTag serialize() {
        CompoundTag tag = new CompoundTag();
        tag.putLong("capacity", capacity);
        tag.putLong("maxInsert", maxInsert);
        tag.putLong("maxExtract", maxExtract);
        tag.putLong("energy", energy);
        return tag;
    }

    @Override
    public void deserialize(CompoundTag tag) {
        capacity = tag.getLong("capacity");
        maxInsert = tag.getLong("maxInsert");
        maxExtract = tag.getLong("maxExtract");
        energy = tag.getLong("energy");
    }

    public void setCapacity(long capacity) {
        this.capacity = capacity;
        onChange();
    }

    public void setMaxInsert(long maxInsert) {
        this.maxInsert = maxInsert;
        onChange();
    }

    public void setMaxExtract(long maxExtract) {
        this.maxExtract = maxExtract;
        onChange();
    }

    public void setMaxTransfer(long maxTransfer) {
        maxInsert = maxTransfer;
        maxExtract = maxTransfer;
        onChange();
    }

    private void onChange() {
        if (changeListener != null) {
            changeListener.run();
        }
    }
}
