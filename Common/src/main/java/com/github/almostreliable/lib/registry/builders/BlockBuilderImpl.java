package com.github.almostreliable.lib.registry.builders;

import com.github.almostreliable.lib.api.registry.RegisterCallback;
import com.github.almostreliable.lib.api.registry.builders.BlockBuilder;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.MaterialColor;

import java.util.Objects;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.function.ToIntFunction;

public class BlockBuilderImpl<B extends Block> extends AbstractEntryBuilder<B> implements BlockBuilder<B> {
    private final Function<BlockBehaviour.Properties, B> factory;
    private BlockBehaviour.Properties properties;

    public BlockBuilderImpl(String id, BlockBehaviour.Properties properties, Function<BlockBehaviour.Properties, B> factory, RegisterCallback<B> registerCallback) {
        super(id, registerCallback);
        this.factory = factory;
        this.properties = properties;
    }

    @Override
    public BlockBuilder<B> properties(Supplier<BlockBehaviour.Properties> supplier) {
        properties = supplier.get();
        Objects.requireNonNull(properties);
        return this;
    }

    @Override
    public BlockBuilder<B> noCollision() {
        properties.noCollission();
        return this;
    }

    @Override
    public BlockBuilder<B> noOcclusion() {
        properties.noOcclusion();
        return this;
    }

    @Override
    public BlockBuilder<B> friction(float friction) {
        properties.friction(friction);
        return this;
    }

    @Override
    public BlockBuilder<B> speedFactor(float speedFactor) {
        properties.speedFactor(speedFactor);
        return this;
    }

    @Override
    public BlockBuilder<B> jumpFactor(float jumpFactor) {
        properties.jumpFactor(jumpFactor);
        return this;
    }

    @Override
    public BlockBuilder<B> sound(SoundType soundType) {
        properties.sound(soundType);
        return this;
    }

    @Override
    public BlockBuilder<B> lightLevel(ToIntFunction<BlockState> toState) {
        properties.lightLevel(toState);
        return this;
    }

    @Override
    public BlockBuilder<B> lightLevel(int lightLevel) {
        properties.lightLevel(value -> lightLevel);
        return this;
    }

    @Override
    public BlockBuilder<B> instabreak() {
        properties.instabreak();
        return this;
    }

    @Override
    public BlockBuilder<B> explosionResistance(float strength) {
        properties.explosionResistance(strength);
        return this;
    }

    @Override
    public BlockBuilder<B> strength(float strength) {
        properties.strength(strength);
        return this;
    }

    @Override
    public BlockBuilder<B> randomTicks() {
        properties.randomTicks();
        return this;
    }

    @Override
    public BlockBuilder<B> dynamicShape() {
        properties.dynamicShape();
        return this;
    }

    @Override
    public BlockBuilder<B> noDrops() {
        properties.noDrops();
        return this;
    }

    @Override
    public BlockBuilder<B> requiresCorrectToolForDrops() {
        properties.requiresCorrectToolForDrops();
        return this;
    }

    @Override
    public BlockBuilder<B> color(MaterialColor materialColor) {
        properties.color(materialColor);
        return this;
    }

    @Override
    public BlockBuilder<B> destroyTime(float destroyTime) {
        properties.destroyTime(destroyTime);
        return this;
    }

    @Override
    public B create() {
        return factory.apply(properties);
    }
}
