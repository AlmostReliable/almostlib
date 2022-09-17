package com.almostreliable.almostlib.registry;

import com.almostreliable.almostlib.AlmostLib;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Supplier;

public class BlockEntityRegistration extends GenericRegistration<BlockEntityType<? extends BlockEntity>> {

    BlockEntityRegistration(String namespace, Registry<BlockEntityType<? extends BlockEntity>> registry) {
        super(namespace, registry);
    }

    public <BE extends BlockEntity> Builder<BE> builder(String name, BiFunction<BlockPos, BlockState, BE> factory) {
        return new Builder<>(name, factory);
    }

    public class Builder<BE extends BlockEntity> {

        private final String name;
        private final BiFunction<BlockPos, BlockState, BE> factory;

        private final List<Supplier<Collection<? extends Block>>> blocksSuppliers = new ArrayList<>();

        public Builder(String name, BiFunction<BlockPos, BlockState, BE> factory) {
            this.name = name;
            this.factory = factory;
        }

        public Builder<BE> blocks(Supplier<Collection<? extends Block>> blocks) {
            blocksSuppliers.add(blocks);
            return this;
        }

        public RegistryEntry<BlockEntityType<BE>> register() {
            return BlockEntityRegistration.this.register(name, () -> {
                Block[] blocks = blocksSuppliers
                        .stream()
                        .flatMap(s -> s.get().stream())
                        .distinct()
                        .toArray(Block[]::new);
                return AlmostLib.PLATFORM.createBlockEntityType(factory, blocks);
            });
        }
    }
}
