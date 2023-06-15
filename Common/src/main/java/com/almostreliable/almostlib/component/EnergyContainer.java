package com.almostreliable.almostlib.component;

public interface EnergyContainer {

    boolean canInsert();

    boolean canExtract();

    void set(long amount);

    long insert(long amount, boolean simulate);

    long extract(long amount, boolean simulate);

    long getAmount();

    long getCapacity();
}
