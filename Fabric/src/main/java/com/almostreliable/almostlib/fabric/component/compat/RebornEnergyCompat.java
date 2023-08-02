package com.almostreliable.almostlib.fabric.component.compat;

import com.almostreliable.almostlib.AlmostLib;
import com.almostreliable.almostlib.component.EnergyContainer;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;

import javax.annotation.Nullable;
import java.util.function.Supplier;

public interface RebornEnergyCompat {

    RebornEnergyCompat EMPTY = new RebornEnergyCompat() {

        @Override
        public void registerEnergyStorage() {}

        @Override
        public EnergyContainer find(BlockEntity blockEntity, @Nullable Direction direction) {
            return null;
        }

        @Override
        public EnergyContainer find(Level world, BlockPos pos, @Nullable Direction direction) {
            return null;
        }
    };

    RebornEnergyCompat INSTANCE = ((Supplier<RebornEnergyCompat>) () -> {
        if (FabricLoader.getInstance().isModLoaded("team_reborn_energy")) {
            return AlmostLib.loadService(RebornEnergyCompat.class);
        }

        return EMPTY;
    }).get();

    void registerEnergyStorage();

    @Nullable
    EnergyContainer find(BlockEntity blockEntity, @Nullable Direction direction);

    @Nullable
    EnergyContainer find(Level world, BlockPos pos, @Nullable Direction direction);
}
