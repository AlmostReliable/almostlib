package com.github.almostreliable.lib.registry.builders;

import com.github.almostreliable.lib.Utils;
import com.github.almostreliable.lib.registry.RegisterCallback;
import com.github.almostreliable.lib.registry.RegistryEntry;
import com.github.almostreliable.lib.registry.RegistryManager;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
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

public class BlockBuilder<B extends Block, I extends BlockItem>
        extends AbstractEntryBuilder<B, Block, BlockBuilder<B, I>> {
    protected final Function<BlockBehaviour.Properties, B> factory;
    protected BlockBehaviour.Properties properties;
    @Nullable
    protected ItemBuilder<I> itemBuilder;
    @Nullable
    protected CreativeModeTab creativeTab;

    public BlockBuilder(String id, BlockBehaviour.Properties properties, Function<BlockBehaviour.Properties, B> factory, RegistryManager manager, RegisterCallback registerCallback) {
        super(id, registerCallback, manager);
        this.factory = factory;
        this.properties = properties;
        item(iItemBuilder -> {});
    }

    public BlockBuilder<B, I> noItem() {
        itemBuilder = null;
        return this;
    }

    public BlockBuilder<B, I> item(Consumer<ItemBuilder<I>> callback) {
        itemBuilder = new ItemBuilder<I>(name,
                manager,
                registerCallback).tab(CreativeModeTab.TAB_BUILDING_BLOCKS);
        callback.accept(itemBuilder);
        return this;
    }

    public BlockBuilder<B, I> properties(Supplier<BlockBehaviour.Properties> supplier) {
        properties = supplier.get();
        Objects.requireNonNull(properties);
        return this;
    }

    public BlockBuilder<B, I> noCollision() {
        properties.noCollission();
        return this;
    }

    public BlockBuilder<B, I> noOcclusion() {
        properties.noOcclusion();
        return this;
    }

    public BlockBuilder<B, I> friction(float friction) {
        properties.friction(friction);
        return this;
    }

    public BlockBuilder<B, I> speedFactor(float speedFactor) {
        properties.speedFactor(speedFactor);
        return this;
    }

    public BlockBuilder<B, I> jumpFactor(float jumpFactor) {
        properties.jumpFactor(jumpFactor);
        return this;
    }

    public BlockBuilder<B, I> sound(SoundType soundType) {
        properties.sound(soundType);
        return this;
    }

    public BlockBuilder<B, I> lightLevel(ToIntFunction<BlockState> toState) {
        properties.lightLevel(toState);
        return this;
    }

    public BlockBuilder<B, I> lightLevel(int lightLevel) {
        properties.lightLevel(value -> lightLevel);
        return this;
    }

    public BlockBuilder<B, I> instabreak() {
        properties.instabreak();
        return this;
    }

    public BlockBuilder<B, I> explosionResistance(float strength) {
        properties.explosionResistance(strength);
        return this;
    }

    public BlockBuilder<B, I> strength(float strength) {
        properties.strength(strength);
        return this;
    }

    public BlockBuilder<B, I> randomTicks() {
        properties.randomTicks();
        return this;
    }

    public BlockBuilder<B, I> dynamicShape() {
        properties.dynamicShape();
        return this;
    }

    public BlockBuilder<B, I> noDrops() {
        properties.noDrops();
        return this;
    }

    public BlockBuilder<B, I> requiresCorrectToolForDrops() {
        properties.requiresCorrectToolForDrops();
        return this;
    }

    public BlockBuilder<B, I> color(MaterialColor materialColor) {
        properties.color(materialColor);
        return this;
    }

    public BlockBuilder<B, I> destroyTime(float destroyTime) {
        properties.destroyTime(destroyTime);
        return this;
    }

    public BlockBuilder<B, I> tab(CreativeModeTab tab) {
        this.creativeTab = tab;
        return this;
    }

    public BlockBuilder<B, I> defaultLang(String value) {
        return lang(Block::getDescriptionId, b -> value);
    }

    public BlockBuilder<B, I> defaultLang() {
        return lang(Block::getDescriptionId, b -> nameToLang());
    }


    @Override
    public B create() {
        return factory.apply(properties);
    }

    @Override
    public ResourceKey<Registry<Block>> getRegistryKey() {
        return Registry.BLOCK_REGISTRY;
    }

    @Override
    public RegistryEntry<B> register() {
        RegistryEntry<B> blockEntry = super.register();
        if (itemBuilder != null) {
            if (creativeTab != null) {
                itemBuilder.tab(creativeTab);
            }
            itemBuilder.setFactory(p -> Utils.cast(new BlockItem(blockEntry.get(), p)));
            itemBuilder.register();
        }
        return blockEntry;
    }
}
