package com.almostreliable.almostlib.forge;

import com.almostreliable.almostlib.component.ComponentLookup;
import com.almostreliable.almostlib.component.EnergyContainer;
import com.almostreliable.almostlib.component.ItemContainerAdapter;
import com.almostreliable.almostlib.forge.component.EnergyContainerAdapterImpl;
import com.almostreliable.almostlib.forge.component.ItemContainerAdapterImpl;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.common.capabilities.ForgeCapabilities;

import javax.annotation.Nullable;

public class ComponentLookupForge implements ComponentLookup {

    @Nullable
    @Override
    public ItemContainerAdapter findItemContainer(BlockEntity blockEntity, @Nullable Direction direction) {
        return blockEntity.getCapability(ForgeCapabilities.ITEM_HANDLER, direction).map(ItemContainerAdapterImpl::new).orElse(null);
    }

    @Nullable
    @Override
    public ItemContainerAdapter findItemContainer(Level world, BlockPos pos, @Nullable Direction direction) {
        BlockEntity blockEntity = world.getBlockEntity(pos);
        if (blockEntity != null) {
            return findItemContainer(blockEntity, direction);
        }

        return null;
    }

    @Nullable
    @Override
    public EnergyContainer findEnergyContainer(BlockEntity blockEntity, @org.jetbrains.annotations.Nullable Direction direction) {
        return blockEntity.getCapability(ForgeCapabilities.ENERGY, direction).map(EnergyContainerAdapterImpl::new).orElse(null);
    }

    @Nullable
    @Override
    public EnergyContainer findEnergyContainer(Level world, BlockPos pos, @org.jetbrains.annotations.Nullable Direction direction) {
        BlockEntity blockEntity = world.getBlockEntity(pos);
        if (blockEntity != null) {
            return findEnergyContainer(blockEntity, direction);
        }

        return null;
    }
}
