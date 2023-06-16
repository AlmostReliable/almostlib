package com.almostreliable.almostlib.fabric.component;

import com.almostreliable.almostlib.component.ComponentLookup;
import com.almostreliable.almostlib.component.EnergyContainer;
import com.almostreliable.almostlib.component.ItemContainerAdapter;
import com.almostreliable.almostlib.fabric.compat.energy.RebornEnergyCompat;
import com.almostreliable.almostlib.fabric.component.ItemContainerAdapterImpl;
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
    public ItemContainerAdapter findItemContainer(BlockEntity blockEntity, @Nullable Direction direction) {
        if (blockEntity.getLevel() == null) {
            return null;
        }

        Storage<ItemVariant> storage = ItemStorage.SIDED.find(blockEntity.getLevel(), blockEntity.getBlockPos(), blockEntity.getBlockState(), blockEntity, direction);
        if (storage != null) {
            return new ItemContainerAdapterImpl(storage);
        }

        return null;
    }

    @Nullable
    @Override
    public ItemContainerAdapter findItemContainer(Level world, BlockPos pos, @Nullable Direction direction) {
        Storage<ItemVariant> storage = ItemStorage.SIDED.find(world, pos, direction);
        if (storage != null) {
            return new ItemContainerAdapterImpl(storage);
        }

        return null;
    }

    @Nullable
    @Override
    public EnergyContainer findEnergyContainer(BlockEntity blockEntity, @Nullable Direction direction) {
        return RebornEnergyCompat.INSTANCE.find(blockEntity, direction);
    }

    @Nullable
    @Override
    public EnergyContainer findEnergyContainer(Level world, BlockPos pos, @Nullable Direction direction) {
        return RebornEnergyCompat.INSTANCE.find(world, pos, direction);
    }
}
