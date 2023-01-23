package com.almostreliable.almostlib.block;

import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DirectionProperty;

import javax.annotation.Nullable;

public class OrientableBlock extends Block {

    public static final DirectionProperty FACING = BlockStateProperties.FACING;
    public static final DirectionProperty TOP = DirectionProperty.create("top", Direction.Plane.HORIZONTAL);

    public OrientableBlock(Properties properties) {
        super(properties);
        registerDefaultState(defaultBlockState().setValue(FACING, Direction.NORTH).setValue(TOP, Direction.NORTH));
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        var state = super.getStateForPlacement(context);
        var facing = context.getNearestLookingDirection().getOpposite();
        var top = context.getHorizontalDirection().getOpposite();
        return (state == null ? defaultBlockState() : state)
            .setValue(FACING, facing)
            .setValue(TOP, facing == Direction.UP ? top.getOpposite() : top);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(FACING).add(TOP);
    }
}
