package com.almostreliable.almostlib.component;

import com.almostreliable.almostlib.AlmostLib;
import com.almostreliable.almostlib.Services;
import com.almostreliable.almostlib.block.TickableEntityBlock;
import com.almostreliable.almostlib.util.Tickable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Registry;
import net.minecraft.world.Container;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;

public class CoalGenerator extends BlockEntity implements ComponentHolder, Tickable {

    private Consumer<Object> forgeInvalidateListener;
    private int foo = 0;

    public CoalGenerator(BlockPos blockPos, BlockState blockState) {
        super(AlmostLib.COAL_BE.get(), blockPos, blockState);
    }

    @Override
    public void setRemoved() {
        super.setRemoved();
        invalidateItemContainers();
    }


    @Override
    public void serverTick() {
        if (level == null) {
            return;
        }

        // every second
        if (level.getGameTime() % 20 == 0) {
            foo++;
        }

        if (level.getGameTime() % 3 != 0) {
            return;
        }


        BlockPos upperPos = getBlockPos().relative(Direction.UP);
        ItemContainerAdapter itemContainer = Services.COMPONENTS.findItemContainer(level, upperPos);
        if (itemContainer != null) {
//            Registry.ITEM.getRandom(level.random).ifPresent(holder -> {
//                Item item = holder.value();
//                itemContainer.insert(item.getDefaultInstance(), 2, false);
//            });

            if (foo > 5) {
                itemContainer.clear();
                foo = 0;
            }
        }
    }


    public Container getItemContainer(@Nullable Direction side) {
        return null;
    }

    @Nullable
    public EnergyContainer getEnergyContainer(@Nullable Direction side) {
        return null;
    }

    @Override
    public void addInvalidateListener(Consumer<Object> listener) {
        this.forgeInvalidateListener = listener;
    }

    @Nullable
    @Override
    public Consumer<Object> getInvalidateListener() {
        return this.forgeInvalidateListener;
    }

    public static class Blog extends Block implements TickableEntityBlock {

        public Blog(Properties properties) {
            super(properties);
        }

        @Nullable
        @Override
        public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
            return new CoalGenerator(pos, state);
        }
    }
}
