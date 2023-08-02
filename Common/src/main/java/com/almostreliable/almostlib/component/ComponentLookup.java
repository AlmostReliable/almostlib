package com.almostreliable.almostlib.component;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;

import javax.annotation.Nullable;

public interface ComponentLookup {

    @Nullable
    default ItemContainer findItemContainer(BlockEntity blockEntity) {
        return findItemContainer(blockEntity, null);
    }

    @Nullable
    ItemContainer findItemContainer(BlockEntity blockEntity, @Nullable Direction direction);

    @Nullable
    default ItemContainer findItemContainer(Level level, BlockPos pos) {
        return findItemContainer(level, pos, null);
    }

    @Nullable
    ItemContainer findItemContainer(Level level, BlockPos pos, @Nullable Direction direction);

    @Nullable
    default EnergyContainer findEnergyContainer(BlockEntity blockEntity) {
        return findEnergyContainer(blockEntity, null);
    }

    @Nullable
    EnergyContainer findEnergyContainer(BlockEntity blockEntity, @Nullable Direction direction);

    @Nullable
    default EnergyContainer findEnergyContainer(Level level, BlockPos pos) {
        return findEnergyContainer(level, pos, null);
    }

    @Nullable
    EnergyContainer findEnergyContainer(Level level, BlockPos pos, @Nullable Direction direction);
}
