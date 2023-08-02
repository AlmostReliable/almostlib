package com.almostreliable.almostlib.component;

public interface EnergyContainer {

    /**
     * Returns whether this container allows insertion of energy.
     *
     * @return True if this container allows insertion, false otherwise.
     */
    boolean allowsInsertion();

    /**
     * Returns whether this container allows extraction of energy.
     *
     * @return True if this container allows extraction, false otherwise.
     */
    boolean allowsExtraction();

    /**
     * Returns the amount of energy stored in this container.
     *
     * @return The amount of energy stored in this container.
     */
    long getAmount();

    /**
     * Returns the maximum amount of energy this container can hold.
     *
     * @return The maximum amount of energy this container can hold.
     */
    long getCapacity();

    /**
     * Inserts the given amount of energy into this container.
     *
     * @param amount   The amount of energy to insert.
     * @param simulate Whether to simulate the insertion.
     * @return The amount of energy that was actually or would have been inserted if simulated.
     */
    long insert(long amount, boolean simulate);

    /**
     * Extracts the given amount of energy from this container.
     *
     * @param amount   The amount of energy to extract.
     * @param simulate Whether to simulate the extraction.
     * @return The amount of energy that was actually or would have been extracted if simulated.
     */
    long extract(long amount, boolean simulate);

    /**
     * Sets the amount of energy stored in this container.
     *
     * @param amount The amount of energy to set.
     */
    void set(long amount);

    /**
     * Clears this container.
     */
    default void clear() {
        set(0);
    }
}
