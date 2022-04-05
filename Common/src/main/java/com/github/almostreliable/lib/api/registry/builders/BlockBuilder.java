package com.github.almostreliable.lib.api.registry.builders;

import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.MaterialColor;

import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.function.ToIntFunction;

public interface BlockBuilder<B extends Block, I extends BlockItem> extends EntryBuilder<B> {

    BlockBuilder<B, I> noItem();

    BlockBuilder<B, I> item(Consumer<ItemBuilder<I>> callback);

    BlockBuilder<B, I> properties(Supplier<BlockBehaviour.Properties> supplier);

    BlockBuilder<B, I> noCollision();

    BlockBuilder<B, I> noOcclusion();

    BlockBuilder<B, I> friction(float friction);

    BlockBuilder<B, I> speedFactor(float speedFactor);

    BlockBuilder<B, I> jumpFactor(float jumpFactor);

    BlockBuilder<B, I> sound(SoundType soundType);

    BlockBuilder<B, I> lightLevel(ToIntFunction<BlockState> toState);

    BlockBuilder<B, I> lightLevel(int lightLevel);

    BlockBuilder<B, I> instabreak();

    BlockBuilder<B, I> explosionResistance(float strength);

    BlockBuilder<B, I> strength(float strength);

    BlockBuilder<B, I> randomTicks();

    BlockBuilder<B, I> dynamicShape();

    BlockBuilder<B, I> noDrops();

    BlockBuilder<B, I> requiresCorrectToolForDrops();

    BlockBuilder<B, I> color(MaterialColor materialColor);

    BlockBuilder<B, I> destroyTime(float destroyTime);

    /**
     * If set, it will be only used if {@link #noItem()} was not called. If custom {@link ItemBuilder} provided, this
     * will overwrite the item builder tab.
     *
     * @param tab the creative tab the item should be shown in
     * @return self
     */
    BlockBuilder<B, I> tab(CreativeModeTab tab);
}
