package testmod.block;

import com.almostreliable.lib.component.ComponentHolder;
import com.almostreliable.lib.component.EnergyContainer;
import com.almostreliable.lib.component.impl.SimpleEnergyContainer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import testmod.TestMod;

import javax.annotation.Nullable;
import java.util.function.Consumer;

public class EnergyBatteryBlock extends Block implements EntityBlock {

    public EnergyBatteryBlock(Properties properties) {
        super(properties);
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new Entity(pos, state);
    }

    public static class Entity extends BlockEntity implements ComponentHolder {

        private final SimpleEnergyContainer energyStorage = new SimpleEnergyContainer(100_000);
        @Nullable private Consumer<Object> invalidationListener;

        public Entity(BlockPos blockPos, BlockState blockState) {
            super(TestMod.ENERGY_BATTERY_ENTITY.get(), blockPos, blockState);
        }

        @Nullable
        @Override
        public EnergyContainer getEnergyContainer(@Nullable Direction side) {
            return energyStorage;
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

        @Override
        protected void saveAdditional(CompoundTag tag) {
            super.saveAdditional(tag);
            tag.put("energyStorage", energyStorage.serialize());
        }

        @Override
        public void load(CompoundTag tag) {
            super.load(tag);
            energyStorage.deserialize(tag.getCompound("energyStorage"));
        }

        public SimpleEnergyContainer getEnergyStorage() {
            return energyStorage;
        }
    }
}
