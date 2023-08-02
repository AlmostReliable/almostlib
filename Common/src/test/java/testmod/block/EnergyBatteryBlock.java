package testmod.block;

import com.almostreliable.almostlib.block.TickableEntityBlock;
import com.almostreliable.almostlib.component.ComponentHolder;
import com.almostreliable.almostlib.component.EnergyContainer;
import com.almostreliable.almostlib.component.SimpleEnergyContainer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import testmod.TestMod;

import javax.annotation.Nullable;
import java.util.function.Consumer;

public class EnergyBatteryBlock extends Block implements TickableEntityBlock {

    public EnergyBatteryBlock(Properties properties) {
        super(properties);
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new Entity(pos, state);
    }

    public static class Entity extends BlockEntity implements ComponentHolder {

        private final EnergyContainer energyContainer = new SimpleEnergyContainer(100_000);
        @Nullable private Consumer<Object> invalidationListener;

        public Entity(BlockPos blockPos, BlockState blockState) {
            super(TestMod.ENERGY_BATTERY_ENTITY.get(), blockPos, blockState);
        }

        @Nullable
        @Override
        public EnergyContainer getEnergyContainer(@Nullable Direction side) {
            return energyContainer;
        }

        @Override
        public void addInvalidateListener(Consumer<Object> listener) {
            invalidationListener = listener;
        }

        @Nullable
        @Override
        public Consumer<Object> getInvalidateListener() {
            return invalidationListener;
        }

        @Override
        public void setRemoved() {
            super.setRemoved();
            invalidateEnergyContainers();
        }
    }
}
