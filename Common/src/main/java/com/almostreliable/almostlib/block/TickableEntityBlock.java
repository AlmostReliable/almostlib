package com.almostreliable.almostlib.block;

import com.almostreliable.almostlib.util.Tickable;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

import javax.annotation.Nullable;

/**
 * Implemented by blocks that have a {@link Tickable} {@link BlockEntity}.
 */
public interface TickableEntityBlock extends EntityBlock {

    @Nullable
    @Override
    default <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> blockEntityType) {
        return (entityLevel, entityState, entityType, entity) -> Tickable.handleTick(entityLevel, entity);
    }
}
