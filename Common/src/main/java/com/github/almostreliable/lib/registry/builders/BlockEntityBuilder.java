package com.github.almostreliable.lib.registry.builders;

import com.github.almostreliable.lib.AlmostLib;
import com.github.almostreliable.lib.registry.RegisterCallback;
import com.github.almostreliable.lib.registry.RegistryManager;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class BlockEntityBuilder<BE extends BlockEntity>
        extends AbstractEntryBuilder<BlockEntityType<BE>, BlockEntityType<?>, BlockEntityBuilder<BE>> {

    private final BiFunction<BlockPos, BlockState, BE> factory;
    @Nullable
    private Function<Stream<Block>, Stream<Block>> filterFunction;
    @Nullable
    private Supplier<Block[]> blocksSupplier;

    public BlockEntityBuilder(String name, BiFunction<BlockPos, BlockState, BE> factory, RegistryManager manager, RegisterCallback registerCallback) {
        super(name, manager, registerCallback);
        this.factory = factory;
    }

    public BlockEntityBuilder<BE> blocks(Function<Stream<Block>, Stream<Block>> filterFunction) {
        this.filterFunction = filterFunction;
        return this;
    }

    public BlockEntityBuilder<BE> blocks(Supplier<Block[]> blocksSupplier) {
        this.blocksSupplier = blocksSupplier;
        return this;
    }

    @Override
    public ResourceKey<Registry<BlockEntityType<?>>> getRegistryKey() {
        return Registry.BLOCK_ENTITY_TYPE_REGISTRY;
    }

    @Override
    public BlockEntityType<BE> create() {
        Set<Block> blocks = new HashSet<>();

        if (filterFunction != null) {
            Set<Block> filteredBlocks = filterFunction
                    .apply(AlmostLib.INSTANCE.getBlocks())
                    .collect(Collectors.toSet());
            blocks.addAll(filteredBlocks);
        }

        if (blocksSupplier != null) {
            Block[] blocksArray = blocksSupplier.get();
            blocks.addAll(Arrays.stream(blocksArray).toList());
        }

        return AlmostLib.INSTANCE.createBlockEntityType(factory, blocks.toArray(Block[]::new));
    }
}
