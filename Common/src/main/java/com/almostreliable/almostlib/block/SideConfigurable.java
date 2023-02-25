package com.almostreliable.almostlib.block;

import net.minecraft.core.Direction;

import javax.annotation.Nullable;

/**
 * An interface for handling side configuration.
 * <p>
 * After implementation, an instance can be passed to a {@link SideConfiguration} constructor.
 */
public interface SideConfigurable {

    /**
     * Gets the current facing direction of the block.
     *
     * @return The current facing direction
     */
    Direction getFacing();

    /**
     * Gets the current direction of the top side of the block.
     * <p>
     * This only needs to be implemented if the block can face up or down.
     *
     * @return The current top direction or null if the block has horizontal facing only
     */
    @Nullable
    default Direction getTop() {
        return null;
    }
}
