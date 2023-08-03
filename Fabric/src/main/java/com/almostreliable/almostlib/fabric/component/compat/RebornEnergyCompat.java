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

/**
 * A compatibility layer for the Team Reborn Energy api.<br>
 * Automatically guards against the missing mod.
 * <p>
 * This interface exposes logic for the component api on how to obtain
 * energy containers.
 */
public interface RebornEnergyCompat {

    /**
     * An empty implementation of {@link RebornEnergyCompat}.
     * <p>
     * This implementation does nothing and is used when the mod is not present.
     */
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

    /**
     * The instance of {@link RebornEnergyCompat}.
     * <p>
     * This instance is lazily loaded and will return {@link #EMPTY} if the mod is not present.
     */
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
