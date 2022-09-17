package com.almostreliable.almostlib.registry;

import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.material.MaterialColor;

import javax.annotation.Nullable;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.function.ToIntFunction;

public class BlockRegistration extends Registration<Block, BlockEntry<? extends Block>> {

    @Nullable private ItemRegistration itemRegistration;

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

    public BlockRegistration itemRegistration(ItemRegistration itemRegistration) {
        this.itemRegistration = itemRegistration;
        return this;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T extends Block> BlockEntry<T> register(String name, Supplier<? extends T> supplier) {
        return (BlockEntry<T>) super.register(name, supplier);
    }

    public <B extends Block> Builder<B> builder(String name, Material material, Function<BlockBehaviour.Properties, ? extends B> factory) {
        return new Builder<>(name, BlockBehaviour.Properties.of(material), factory);
    }

    public class Builder<B extends Block> {
        private final String name;
        private BlockBehaviour.Properties properties;
        Function<BlockBehaviour.Properties, ? extends B> factory;

        /* Item Stuff */
        @Nullable Consumer<ItemRegistration.Builder<? extends BlockItem>> itemBuilderConsumer;

        public Builder(String name, BlockBehaviour.Properties properties, Function<BlockBehaviour.Properties, ? extends B> factory) {
            this.name = name;
            this.properties = properties;
            this.factory = factory;
        }

        public Builder<B> item(Consumer<ItemRegistration.Builder<? extends BlockItem>> consumer) {
            itemBuilderConsumer = consumer;
            return this;
        }

        public Builder<B> simpleItem() {
            return item(builder -> {});
        }

        public Builder<B> properties(Supplier<BlockBehaviour.Properties> supplier) {
            properties = supplier.get();
            Objects.requireNonNull(properties);
            return this;
        }

        public Builder<B> noCollision() {
            properties.noCollission();
            return this;
        }

        public Builder<B> noOcclusion() {
            properties.noOcclusion();
            return this;
        }

        public Builder<B> friction(float friction) {
            properties.friction(friction);
            return this;
        }

        public Builder<B> speedFactor(float speedFactor) {
            properties.speedFactor(speedFactor);
            return this;
        }

        public Builder<B> jumpFactor(float jumpFactor) {
            properties.jumpFactor(jumpFactor);
            return this;
        }

        public Builder<B> sound(SoundType soundType) {
            properties.sound(soundType);
            return this;
        }

        public Builder<B> lightLevel(ToIntFunction<BlockState> toState) {
            properties.lightLevel(toState);
            return this;
        }

        public Builder<B> lightLevel(int lightLevel) {
            properties.lightLevel(value -> lightLevel);
            return this;
        }

        public Builder<B> breaksInstantly() {
            properties.instabreak();
            return this;
        }

        public Builder<B> explosionResistance(float strength) {
            properties.explosionResistance(strength);
            return this;
        }

        public Builder<B> strength(float strength) {
            properties.strength(strength);
            return this;
        }

        public Builder<B> randomTicks() {
            properties.randomTicks();
            return this;
        }

        public Builder<B> dynamicShape() {
            properties.dynamicShape();
            return this;
        }

        public Builder<B> noDrops() {
            properties.noDrops();
            return this;
        }

        public Builder<B> requiresCorrectToolForDrops() {
            properties.requiresCorrectToolForDrops();
            return this;
        }

        public Builder<B> color(MaterialColor materialColor) {
            properties.color(materialColor);
            return this;
        }

        public Builder<B> color(DyeColor dyeColor) {
            return color(dyeColor.getMaterialColor());
        }

        public Builder<B> destroyTime(float destroyTime) {
            properties.destroyTime(destroyTime);
            return this;
        }

        public BlockEntry<B> register() {
            BlockEntry<B> block = BlockRegistration.this.register(name, () -> factory.apply(properties));

            var ir = BlockRegistration.this.itemRegistration;
            if (ir == null && itemBuilderConsumer != null) {
                throw new IllegalStateException(
                        "Cannot register item for block " + block.getId() + " without an item registration");
            }
            if (itemBuilderConsumer != null) {
                var itemBuilder = ir.builder(name, (props) -> new BlockItem(block.get(), props));
                itemBuilderConsumer.accept(itemBuilder);
                itemBuilder.register();
            }

            return block;
        }
    }
}
