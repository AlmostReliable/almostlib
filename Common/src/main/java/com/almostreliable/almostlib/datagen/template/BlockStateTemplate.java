package com.almostreliable.almostlib.datagen.template;

import com.almostreliable.almostlib.datagen.provider.BlockStateProvider;
import com.almostreliable.almostlib.registry.BlockEntry;
import net.minecraft.world.level.block.Block;

import java.util.function.BiConsumer;

@FunctionalInterface
public interface BlockStateTemplate extends BiConsumer<BlockEntry<Block>, BlockStateProvider> {

}
