package com.almostreliable.almostlib.component;

public class SimpleEnergyContainer implements EnergyContainer {

    protected long capacity;
    protected long maxInsert;
    protected long maxExtract;
    protected long energy;

    public SimpleEnergyContainer(long capacity) {
        this(capacity, capacity);
    }

    public SimpleEnergyContainer(long capacity, long maxTransfer) {
        this(capacity, maxTransfer, maxTransfer);
    }

    public SimpleEnergyContainer(long capacity, long maxInsert, long maxExtract) {
        this(capacity, maxInsert, maxExtract, 0);
    }

    public SimpleEnergyContainer(long capacity, long maxInsert, long maxExtract, long energy) {
        this.capacity = capacity;
        this.maxInsert = maxInsert;
        this.maxExtract = maxExtract;
        this.energy = energy;
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
        }

        return toExtract;
    }

    @Override
    public void set(long amount) {
        energy = amount;
    }
}
