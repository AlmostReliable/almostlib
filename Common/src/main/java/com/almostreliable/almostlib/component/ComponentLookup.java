package com.almostreliable.almostlib.component;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;

import javax.annotation.Nullable;

public interface ComponentLookup {

    @Nullable
    default ItemContainerAdapter findItemContainer(BlockEntity blockEntity) {
        return findItemContainer(blockEntity, null);
    }

    @Nullable
    ItemContainerAdapter findItemContainer(BlockEntity blockEntity, @Nullable Direction direction);

    @Nullable
    default ItemContainerAdapter findItemContainer(Level world, BlockPos pos) {
        return findItemContainer(world, pos, null);
    }

    @Nullable
    ItemContainerAdapter findItemContainer(Level world, BlockPos pos, @Nullable Direction direction);

    @Nullable
    default EnergyContainer findEnergyContainer(BlockEntity blockEntity) {
        return findEnergyContainer(blockEntity, null);
    }

    @Nullable
    EnergyContainer findEnergyContainer(BlockEntity blockEntity, @Nullable Direction direction);

    @Nullable
    default EnergyContainer findEnergyContainer(Level world, BlockPos pos) {
        return findEnergyContainer(world, pos, null);
    }

    @Nullable
    EnergyContainer findEnergyContainer(Level world, BlockPos pos, @Nullable Direction direction);
}
