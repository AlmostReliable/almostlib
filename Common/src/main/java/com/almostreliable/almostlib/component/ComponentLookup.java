package com.almostreliable.almostlib.component;

import com.almostreliable.almostlib.Services;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;

import javax.annotation.Nullable;

/**
 * Implemented by the respective loaders to define how to look up components.
 * <p>
 * Can be used to access components from the environment.<br>
 * Accessible via an auto service in {@link Services}.
 */
public interface ComponentLookup {

    @Nullable
    ItemContainer findItemContainer(BlockEntity blockEntity, @Nullable Direction direction);

    @Nullable
    default ItemContainer findItemContainer(BlockEntity blockEntity) {
        return findItemContainer(blockEntity, null);
    }

    @Nullable
    ItemContainer findItemContainer(Level level, BlockPos pos, @Nullable Direction direction);

    @Nullable
    default ItemContainer findItemContainer(Level level, BlockPos pos) {
        return findItemContainer(level, pos, null);
    }

    @Nullable
    EnergyContainer findEnergyContainer(BlockEntity blockEntity, @Nullable Direction direction);

    @Nullable
    default EnergyContainer findEnergyContainer(BlockEntity blockEntity) {
        return findEnergyContainer(blockEntity, null);
    }

    @Nullable
    EnergyContainer findEnergyContainer(Level level, BlockPos pos, @Nullable Direction direction);

    @Nullable
    default EnergyContainer findEnergyContainer(Level level, BlockPos pos) {
        return findEnergyContainer(level, pos, null);
    }
}
