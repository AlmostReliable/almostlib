package testmod.block;

import com.almostreliable.almostlib.block.TickableEntityBlock;
import com.almostreliable.almostlib.util.Tickable;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;
import testmod.TestMod;

public class TestEnergyGeneratorBlock extends Block implements TickableEntityBlock {

    public TestEnergyGeneratorBlock(Properties properties) {
        super(properties);
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new Entity(pos, state);
    }

    public static class Entity extends BlockEntity implements Tickable {

        public Entity(BlockPos blockPos, BlockState blockState) {
            super(TestMod.ENERGY_GENERATOR_ENTITY.get(), blockPos, blockState);
        }

        @Override
        public void serverTick() {

        }
    }
}
