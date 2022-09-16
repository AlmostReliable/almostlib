package com.almostreliable.almostlib.registry;

import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;

import java.util.function.Supplier;

public class BlockRegistration extends Registration<Block, BlockEntry<? extends Block>> {

    BlockRegistration(String namespace, Registry<Block> registry) {
        super(namespace, registry);
    }

    @Override
    protected BlockEntry<? extends Block> createEntry(ResourceLocation id, Supplier<? extends Block> supplier) {
        return new BlockEntry<>() {
            @Override
            public ResourceLocation getId() {
                return id;
            }

            @Override
            public Block get() {
                return supplier.get();
            }
        };
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T extends Block> BlockEntry<T> register(String name, Supplier<? extends T> supplier) {
        return (BlockEntry<T>) super.register(name, supplier);
    }
}
