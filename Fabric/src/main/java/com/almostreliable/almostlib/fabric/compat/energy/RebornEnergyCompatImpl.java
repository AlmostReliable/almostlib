package com.almostreliable.almostlib.fabric.compat.energy;

import com.almostreliable.almostlib.component.ComponentHolder;
import com.almostreliable.almostlib.component.EnergyContainer;
import com.google.auto.service.AutoService;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.jetbrains.annotations.Nullable;
import team.reborn.energy.api.EnergyStorage;

@AutoService(RebornEnergyCompat.class)
public class RebornEnergyCompatImpl implements RebornEnergyCompat {

    @Override
    public void registerEnergyStorage() {
        EnergyStorage.SIDED.registerFallback(
            (world, pos, state, blockEntity, direction) -> {
                if (blockEntity instanceof ComponentHolder componentHolder) {
                    var container = componentHolder.getEnergyContainer(direction);
                    if (container != null) {
                        return new EnergyContainerWrapper(container);
                    }
                }

                return null;
            });
    }

    @Nullable
    @Override
    public EnergyContainer find(BlockEntity blockEntity, @Nullable Direction direction) {
        if (blockEntity.getLevel() == null) {
            return null;
        }

        var storage = EnergyStorage.SIDED.find(blockEntity.getLevel(), blockEntity.getBlockPos(), blockEntity.getBlockState(), blockEntity, direction);
        if (storage != null) {
            return new EnergyStorageWrapper(storage);
        }

        return null;
    }

    @Nullable
    @Override
    public EnergyContainer find(Level world, BlockPos pos, @Nullable Direction direction) {
        var storage = EnergyStorage.SIDED.find(world, pos, world.getBlockState(pos), null, direction);
        if (storage != null) {
            return new EnergyStorageWrapper(storage);
        }

        return null;
    }
}
