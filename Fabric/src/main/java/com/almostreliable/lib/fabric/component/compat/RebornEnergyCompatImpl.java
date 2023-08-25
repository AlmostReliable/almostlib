package com.almostreliable.lib.fabric.component.compat;

import com.almostreliable.lib.component.ComponentHolder;
import com.almostreliable.lib.component.EnergyContainer;
import com.google.auto.service.AutoService;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import team.reborn.energy.api.EnergyStorage;

import javax.annotation.Nullable;

@AutoService(RebornEnergyCompat.class)
public class RebornEnergyCompatImpl implements RebornEnergyCompat {

    @Override
    public void registerEnergyStorage() {
        EnergyStorage.SIDED.registerFallback(
            (level, pos, state, blockEntity, direction) -> {
                if (!(blockEntity instanceof ComponentHolder componentHolder)) {
                    return null;
                }

                var container = componentHolder.getEnergyContainer(direction);
                if (container == null) return null;

                return new EnergyContainerWrapper(container);
            });
    }

    @Nullable
    @Override
    public EnergyContainer find(BlockEntity blockEntity, @Nullable Direction direction) {
        if (blockEntity.getLevel() == null) {
            return null;
        }

        var storage = EnergyStorage.SIDED.find(blockEntity.getLevel(), blockEntity.getBlockPos(), blockEntity.getBlockState(), blockEntity, direction);
        if (storage == null) return null;

        return new EnergyStorageWrapper(storage);
    }

    @Nullable
    @Override
    public EnergyContainer find(Level level, BlockPos pos, @Nullable Direction direction) {
        var storage = EnergyStorage.SIDED.find(level, pos, level.getBlockState(pos), null, direction);
        if (storage == null) return null;

        return new EnergyStorageWrapper(storage);
    }
}
