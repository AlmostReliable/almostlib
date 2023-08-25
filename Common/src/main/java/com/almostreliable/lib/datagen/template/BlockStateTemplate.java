package com.almostreliable.lib.datagen.template;

import com.almostreliable.lib.datagen.provider.BlockStateProvider;
import com.almostreliable.lib.registry.BlockEntry;
import net.minecraft.world.level.block.Block;

import java.util.function.BiConsumer;

@FunctionalInterface
public interface BlockStateTemplate extends BiConsumer<BlockEntry<Block>, BlockStateProvider> {

}
