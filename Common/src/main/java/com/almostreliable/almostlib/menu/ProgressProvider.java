package com.almostreliable.almostlib.menu;

/**
 * An interface for handling progress tracking.
 * <p>
 * This can be implemented by menus to ensure progress between 0 and 100 percent can be properly calculated.
 */
public interface ProgressProvider {

    /**
     * Gets the current progress as an integer representation.
     * <p>
     * This needs to be a value between  and {@link #getMaxProgress()}.
     *
     * @return The current progress as an integer.
     */
    int getProgress();

    /**
     * Gets the maximum progress as an integer representation.
     * <p>
     * This needs to be a value greater than 0.
     *
     * @return The maximum progress as an integer.
     */
    int getMaxProgress();
}
