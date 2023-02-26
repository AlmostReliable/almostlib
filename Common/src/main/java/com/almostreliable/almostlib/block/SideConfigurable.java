package com.almostreliable.almostlib.block;

import net.minecraft.core.Direction;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;

import javax.annotation.Nullable;

/**
 * Implemented by {@link Block}s or {@link BlockEntity}s that can be configured to
 * have different behavior on each side.
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
