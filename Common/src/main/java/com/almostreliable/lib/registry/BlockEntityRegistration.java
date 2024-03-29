package com.almostreliable.lib.registry;

import com.almostreliable.lib.AlmostLib;
import com.almostreliable.lib.util.AlmostUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Supplier;

public class BlockEntityRegistration extends Registration<BlockEntityType<?>, BlockEntityEntry<? extends BlockEntity>> {

    BlockEntityRegistration(String namespace, Registry<BlockEntityType<?>> registry) {
        super(namespace, registry);
    }

    public <BE extends BlockEntity> BlockEntityEntry<BE> register(String id, Supplier<? extends BlockEntityType<?>> supplier) {
        return AlmostUtils.cast(createOrThrowEntry(id, supplier));
    }

    public <BE extends BlockEntity> Builder<BE> builder(String id, BiFunction<BlockPos, BlockState, BE> factory) {
        return new Builder<>(id, factory);
    }

    @Override
    protected BlockEntityEntry<? extends BlockEntity> createEntry(ResourceLocation id, Supplier<? extends BlockEntityType<?>> supplier) {
        return new BlockEntityEntry<>(id, AlmostUtils.cast(supplier));
    }

    public class Builder<BE extends BlockEntity> {

        private final String id;
        private final BiFunction<BlockPos, BlockState, BE> factory;

        private final List<Supplier<Collection<BlockEntry<? extends Block>>>> blockEntrySuppliers = new ArrayList<>();

        public Builder(String id, BiFunction<BlockPos, BlockState, BE> factory) {
            this.id = id;
            this.factory = factory;
        }

        public Builder<BE> block(BlockEntry<? extends Block> block) {
            blockEntrySuppliers.add(() -> List.of(block));
            return this;
        }

        public Builder<BE> blocks(Supplier<Collection<BlockEntry<? extends Block>>> blocks) {
            blockEntrySuppliers.add(blocks);
            return this;
        }

        public BlockEntityEntry<BE> register() {
            return BlockEntityRegistration.this.register(id, () -> {
                Block[] blocks = blockEntrySuppliers
                    .stream()
                    .flatMap(s -> s.get().stream())
                    .map(BlockEntry::get)
                    .distinct()
                    .toArray(Block[]::new);
                return AlmostLib.PLATFORM.createBlockEntityType(factory, blocks);
            });
        }
    }
}
