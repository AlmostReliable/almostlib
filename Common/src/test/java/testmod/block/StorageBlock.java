package testmod.block;

import com.almostreliable.almostlib.component.ComponentHolder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import testmod.TestMod;

import javax.annotation.Nullable;
import java.util.function.Consumer;

public class StorageBlock extends Block implements EntityBlock {

    public StorageBlock(Properties properties) {
        super(properties);
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new Entity(pos, state);
    }

    public static class Entity extends BlockEntity implements ComponentHolder {

        private final SimpleContainer inventory = new SimpleContainer(3);
        @Nullable private Consumer<Object> invalidationListener;

        public Entity(BlockPos blockPos, BlockState blockState) {
            super(TestMod.STORAGE_ENTITY.get(), blockPos, blockState);
        }

        @Nullable
        @Override
        public Container getItemContainer(@Nullable Direction side) {
            return inventory;
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
            tag.put("inventory", inventory.createTag());
        }

        @Override
        public void load(CompoundTag tag) {
            super.load(tag);
            inventory.fromTag(tag.getList("inventory", Tag.TAG_COMPOUND));
        }

        public Container getInventory() {
            return inventory;
        }
    }
}
