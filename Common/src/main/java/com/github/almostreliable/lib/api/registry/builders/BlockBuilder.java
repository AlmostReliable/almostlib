package com.github.almostreliable.lib.api.registry.builders;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.MaterialColor;

import java.util.function.Supplier;
import java.util.function.ToIntFunction;

public interface BlockBuilder<B extends Block> extends EntryBuilder<B> {

    BlockBuilder<B> properties(Supplier<BlockBehaviour.Properties> supplier);

    BlockBuilder<B> noCollision();

    BlockBuilder<B> noOcclusion();

    BlockBuilder<B> friction(float friction);

    BlockBuilder<B> speedFactor(float speedFactor);

    BlockBuilder<B> jumpFactor(float jumpFactor);

    BlockBuilder<B> sound(SoundType soundType);

    BlockBuilder<B> lightLevel(ToIntFunction<BlockState> toState);

    BlockBuilder<B> lightLevel(int lightLevel);

    BlockBuilder<B> instabreak();

    BlockBuilder<B> explosionResistance(float strength);

    BlockBuilder<B> strength(float strength);

    BlockBuilder<B> randomTicks();

    BlockBuilder<B> dynamicShape();

    BlockBuilder<B> noDrops();

    BlockBuilder<B> requiresCorrectToolForDrops();

    BlockBuilder<B> color(MaterialColor materialColor);

    BlockBuilder<B> destroyTime(float destroyTime);
}
