package com.almostreliable.lib.gametest;

import net.minecraft.core.BlockPos;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestAssertException;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.gametest.framework.GameTestInfo;
import net.minecraft.world.level.block.entity.BlockEntity;

/**
 * A custom implementation of {@link GameTestHelper} that can be used to
 * add more utility methods for game tests.
 * <p>
 * It is automatically passed to {@link GameTest}
 * annotated methods inside implementations of {@link GameTestProvider}s.
 */
public class AlmostGameTestHelper extends GameTestHelper {

    private final GameTestInfo testInfo;

    public AlmostGameTestHelper(GameTestInfo testInfo) {
        super(testInfo);
        this.testInfo = testInfo;
    }

    /**
     * Convenience method to get a {@link BlockEntity} at the given position.
     * <p>
     * Will automatically fail if the {@link BlockEntity} is not of the expected type.
     *
     * @param pos   The position of the block entity.
     * @param clazz The expected type of the block entity.
     * @param <T>   The type of the block entity.
     * @return The block entity at the given position.
     */
    public <T extends BlockEntity> T getBlockEntity(BlockPos pos, Class<T> clazz) {
        BlockEntity be = getBlockEntity(pos);
        if (!clazz.isInstance(be)) {
            throw new GameTestAssertException("Expected block entity of type " + clazz.getName() + " at " + pos + ".");
        }

        return clazz.cast(be);
    }

    /**
     * Runs the given task at the given tick time and then succeeds the test.
     * <p>
     * Avoids expected tick time failures that come with {@link #succeedOnTickWhen(int, Runnable)}.
     *
     * @param tickTime The tick time to run the task at.
     * @param task     The task to run.
     */
    public void succeedAtTickTime(long tickTime, Runnable task) {
        runAtTickTime(tickTime, () -> {
            task.run();
            succeed();
        });
    }
}
