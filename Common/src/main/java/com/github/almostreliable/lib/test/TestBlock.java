package com.github.almostreliable.lib.test;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

public class TestBlock extends Block implements EntityBlock {
    public TestBlock(Properties $$0) {
        super($$0);
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos var1, BlockState var2) {
        return new TestBlockEntity(var1, var2);
    }
}
