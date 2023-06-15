package com.almostreliable.almostlib.component;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.Container;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;

public class CoalGenerator extends BlockEntity implements ComponentHolder {

    private Consumer<Object> forgeInvalidateListener;

    public CoalGenerator(BlockEntityType<?> blockEntityType, BlockPos blockPos, BlockState blockState) {
        super(blockEntityType, blockPos, blockState);
    }

    @Override
    public void setRemoved() {
        super.setRemoved();
        invalidateItemContainers();
    }

    public Container getItemContainer(@Nullable Direction side) {
        return null;
    }

    @Nullable
    public EnergyContainer getEnergyContainer(@Nullable Direction side) {
        return null;
    }

    @Override
    public void addInvalidateListener(Consumer<Object> listener) {
        this.forgeInvalidateListener = listener;
    }

    @Nullable
    @Override
    public Consumer<Object> getInvalidateListener() {
        return this.forgeInvalidateListener;
    }
}
