package com.almostreliable.almostlib.forge.component;

import com.almostreliable.almostlib.component.ComponentLookup;
import com.almostreliable.almostlib.component.EnergyContainer;
import com.almostreliable.almostlib.component.ItemContainer;
import com.google.auto.service.AutoService;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.common.capabilities.ForgeCapabilities;

import javax.annotation.Nullable;


@AutoService(ComponentLookup.class)
public class ComponentLookupForge implements ComponentLookup {

    @Nullable
    @Override
    public ItemContainer findItemContainer(BlockEntity blockEntity, @Nullable Direction direction) {
        return blockEntity.getCapability(ForgeCapabilities.ITEM_HANDLER, direction).map(ItemHandlerWrapper::new).orElse(null);
    }

    @Nullable
    @Override
    public ItemContainer findItemContainer(Level world, BlockPos pos, @Nullable Direction direction) {
        BlockEntity blockEntity = world.getBlockEntity(pos);
        if (blockEntity != null) {
            return findItemContainer(blockEntity, direction);
        }

        return null;
    }

    @Nullable
    @Override
    public EnergyContainer findEnergyContainer(BlockEntity blockEntity, @Nullable Direction direction) {
        return blockEntity.getCapability(ForgeCapabilities.ENERGY, direction).map(EnergyStorageWrapper::new).orElse(null);
    }

    @Nullable
    @Override
    public EnergyContainer findEnergyContainer(Level world, BlockPos pos, @Nullable Direction direction) {
        BlockEntity blockEntity = world.getBlockEntity(pos);
        if (blockEntity != null) {
            return findEnergyContainer(blockEntity, direction);
        }

        return null;
    }
}
