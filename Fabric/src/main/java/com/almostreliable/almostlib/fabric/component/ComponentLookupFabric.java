package com.almostreliable.almostlib.fabric.component;

import com.almostreliable.almostlib.component.ComponentLookup;
import com.almostreliable.almostlib.component.EnergyContainer;
import com.almostreliable.almostlib.component.ItemContainer;
import com.almostreliable.almostlib.fabric.component.compat.RebornEnergyCompat;
import com.google.auto.service.AutoService;
import net.fabricmc.fabric.api.transfer.v1.item.ItemStorage;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;

import javax.annotation.Nullable;

@SuppressWarnings("UnstableApiUsage")
@AutoService(ComponentLookup.class)
public class ComponentLookupFabric implements ComponentLookup {

    @Nullable
    @Override
    public ItemContainer findItemContainer(BlockEntity blockEntity, @Nullable Direction direction) {
        if (blockEntity.getLevel() == null) return null;

        Storage<ItemVariant> storage = ItemStorage.SIDED.find(
            blockEntity.getLevel(),
            blockEntity.getBlockPos(),
            blockEntity.getBlockState(),
            blockEntity,
            direction
        );
        if (storage == null) return null;

        return new ItemStorageWrapper(storage);
    }

    @Nullable
    @Override
    public ItemContainer findItemContainer(Level level, BlockPos pos, @Nullable Direction direction) {
        Storage<ItemVariant> storage = ItemStorage.SIDED.find(level, pos, direction);
        if (storage == null) return null;
        return new ItemStorageWrapper(storage);
    }

    @Nullable
    @Override
    public EnergyContainer findEnergyContainer(BlockEntity blockEntity, @Nullable Direction direction) {
        return RebornEnergyCompat.INSTANCE.find(blockEntity, direction);
    }

    @Nullable
    @Override
    public EnergyContainer findEnergyContainer(Level level, BlockPos pos, @Nullable Direction direction) {
        return RebornEnergyCompat.INSTANCE.find(level, pos, direction);
    }
}
