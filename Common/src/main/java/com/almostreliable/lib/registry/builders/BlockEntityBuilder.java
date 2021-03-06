package com.almostreliable.lib.registry.builders;

import com.almostreliable.lib.Utils;
import com.almostreliable.lib.registry.RegistryEntry;
import com.almostreliable.lib.AlmostLib;
import com.almostreliable.lib.registry.AlmostManager;
import com.almostreliable.lib.registry.RegisterCallback;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

import javax.annotation.Nullable;
import java.util.*;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class BlockEntityBuilder<BE extends BlockEntity>
        extends AbstractEntryBuilder<BlockEntityType<BE>, BlockEntityType<?>, BlockEntityBuilder<BE>> {

    private final BiFunction<BlockPos, BlockState, BE> factory;
    @Nullable
    private Predicate<? extends Block> filterFunction;
    @Nullable
    private List<Supplier<? extends Block>> blocksSuppliers;
    @Nullable
    private Supplier<Function<BlockEntityRendererProvider.Context, BlockEntityRenderer<BE>>> rendererProvider;

    public BlockEntityBuilder(String name, BiFunction<BlockPos, BlockState, BE> factory, AlmostManager manager, RegisterCallback registerCallback) {
        super(name, manager, registerCallback);
        this.factory = factory;
    }

    public BlockEntityBuilder<BE> blocks(Predicate<? extends Block> filterFunction) {
        this.filterFunction = filterFunction;
        return this;
    }

    @SafeVarargs
    public final <B extends Block> BlockEntityBuilder<BE> blocks(Supplier<B>... additionalSuppliers) {
        if (blocksSuppliers == null) {
            blocksSuppliers = new ArrayList<>();
        }

        blocksSuppliers.addAll(Arrays.asList(additionalSuppliers));
        return this;
    }

    public BlockEntityBuilder<BE> renderer(Supplier<Function<BlockEntityRendererProvider.Context, BlockEntityRenderer<BE>>> rendererProvider) {
        this.rendererProvider = rendererProvider;
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
            Set<Block> filteredBlocks = AlmostLib.INSTANCE
                    .getBlocks()
                    .filter(o -> filterFunction.test(Utils.cast(o)))
                    .collect(Collectors.toSet());
            blocks.addAll(filteredBlocks);
        }

        if (blocksSuppliers != null) {
            for (var supplier : blocksSuppliers) {
                blocks.add(supplier.get());
            }
        }

        return AlmostLib.INSTANCE.createBlockEntityType(factory, blocks.toArray(Block[]::new));
    }

    @Override
    public void onRegister(RegistryEntry<BlockEntityType<BE>> registryEntry) {
        if (rendererProvider != null) {
            manager.addOnClientInit(clientManager -> {
                clientManager.registerBlockEntityRenderer(registryEntry.get(), rendererProvider.get());
            });
        }
    }
}
