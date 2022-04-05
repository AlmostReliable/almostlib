package com.github.almostreliable.lib.registry.builders;

import com.github.almostreliable.lib.api.registry.RegisterCallback;
import com.github.almostreliable.lib.api.registry.builders.BlockBuilder;
import com.github.almostreliable.lib.api.registry.builders.ItemBuilder;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.MaterialColor;

import javax.annotation.Nullable;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.function.ToIntFunction;

public class BlockBuilderImpl<B extends Block, I extends BlockItem> extends AbstractEntryBuilder<B, Block>
        implements BlockBuilder<B, I> {
    private final Function<BlockBehaviour.Properties, B> factory;
    private final RegisterCallback<I> registerItemCallback;
    private BlockBehaviour.Properties properties;
    @Nullable
    private ItemBuilder<I> itemBuilder;

    public BlockBuilderImpl(String id, BlockBehaviour.Properties properties, Function<BlockBehaviour.Properties, B> factory, RegisterCallback<B> registerCallback, RegisterCallback<I> registerItemCallback) {
        super(id, registerCallback);
        this.factory = factory;
        this.properties = properties;
        this.registerItemCallback = registerItemCallback;
//        this.itemBuilder = new ItemBuilderImpl<>(id, p -> new BlockItem())
    }

    @Override
    public BlockBuilder<B, I> noItem() {
        itemBuilder = null;
        return this;
    }

    @Override
    public BlockBuilder<B, I> item(Consumer<ItemBuilder<I>> callback) {
//        new ItemBuilderImpl<>()
        return this;
    }

    @Override
    public BlockBuilder<B, I> properties(Supplier<BlockBehaviour.Properties> supplier) {
        properties = supplier.get();
        Objects.requireNonNull(properties);
        return this;
    }

    @Override
    public BlockBuilder<B, I> noCollision() {
        properties.noCollission();
        return this;
    }

    @Override
    public BlockBuilder<B, I> noOcclusion() {
        properties.noOcclusion();
        return this;
    }

    @Override
    public BlockBuilder<B, I> friction(float friction) {
        properties.friction(friction);
        return this;
    }

    @Override
    public BlockBuilder<B, I> speedFactor(float speedFactor) {
        properties.speedFactor(speedFactor);
        return this;
    }

    @Override
    public BlockBuilder<B, I> jumpFactor(float jumpFactor) {
        properties.jumpFactor(jumpFactor);
        return this;
    }

    @Override
    public BlockBuilder<B, I> sound(SoundType soundType) {
        properties.sound(soundType);
        return this;
    }

    @Override
    public BlockBuilder<B, I> lightLevel(ToIntFunction<BlockState> toState) {
        properties.lightLevel(toState);
        return this;
    }

    @Override
    public BlockBuilder<B, I> lightLevel(int lightLevel) {
        properties.lightLevel(value -> lightLevel);
        return this;
    }

    @Override
    public BlockBuilder<B, I> instabreak() {
        properties.instabreak();
        return this;
    }

    @Override
    public BlockBuilder<B, I> explosionResistance(float strength) {
        properties.explosionResistance(strength);
        return this;
    }

    @Override
    public BlockBuilder<B, I> strength(float strength) {
        properties.strength(strength);
        return this;
    }

    @Override
    public BlockBuilder<B, I> randomTicks() {
        properties.randomTicks();
        return this;
    }

    @Override
    public BlockBuilder<B, I> dynamicShape() {
        properties.dynamicShape();
        return this;
    }

    @Override
    public BlockBuilder<B, I> noDrops() {
        properties.noDrops();
        return this;
    }

    @Override
    public BlockBuilder<B, I> requiresCorrectToolForDrops() {
        properties.requiresCorrectToolForDrops();
        return this;
    }

    @Override
    public BlockBuilder<B, I> color(MaterialColor materialColor) {
        properties.color(materialColor);
        return this;
    }

    @Override
    public BlockBuilder<B, I> destroyTime(float destroyTime) {
        properties.destroyTime(destroyTime);
        return this;
    }

    @Override
    public B create() {
        return factory.apply(properties);
    }

    @Override
    protected ResourceKey<Registry<Block>> getRegistryKey() {
        return Registry.BLOCK_REGISTRY;
    }
}
