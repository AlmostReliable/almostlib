package com.almostreliable.almostlib.gametest;

import net.minecraft.core.BlockPos;
import net.minecraft.gametest.framework.GameTestAssertException;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.gametest.framework.GameTestInfo;
import net.minecraft.world.level.block.entity.BlockEntity;

public class AlmostGameTestHelper extends GameTestHelper {

    private final GameTestInfo testInfo;

    public AlmostGameTestHelper(GameTestInfo testInfo) {
        super(testInfo);
        this.testInfo = testInfo;
    }

    public <T extends BlockEntity> T getBlockEntity(BlockPos pos, Class<T> clazz) {
        BlockEntity be = getBlockEntity(pos);
        if (!clazz.isInstance(be)) {
            throw new GameTestAssertException("Expected block entity of type " + clazz.getSimpleName() + " at " + pos + ".");
        }

        return clazz.cast(be);
    }

    public void succeedAtTickTime(long tickTime, Runnable task) {
        runAtTickTime(tickTime, () -> {
            task.run();
            succeed();
        });
    }
}
