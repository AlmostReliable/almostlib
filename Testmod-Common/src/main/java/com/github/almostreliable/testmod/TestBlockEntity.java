package com.github.almostreliable.testmod;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class TestBlockEntity extends BlockEntity {
    public TestBlockEntity(BlockPos $$1, BlockState $$2) {
        super(TestModCommon.TEST_BLOCK_ENTITY.get(), $$1, $$2);
    }
}
