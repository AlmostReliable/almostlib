package com.almostreliable.almostlib.registry;

import com.almostreliable.almostlib.datagen.DataGenHolder;
import com.almostreliable.almostlib.datagen.DataGenManager;
import com.almostreliable.almostlib.datagen.provider.BlockStateProvider;
import com.almostreliable.almostlib.datagen.provider.LootTableProvider;
import com.almostreliable.almostlib.util.AlmostUtils;
import com.almostreliable.almostlib.util.ToolType;
import net.minecraft.core.Registry;
import net.minecraft.data.models.model.DelegatedModel;
import net.minecraft.data.models.model.ModelLocationUtils;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.item.*;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.DropExperienceBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.material.MaterialColor;
import net.minecraft.world.level.storage.loot.LootTable;

import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.function.*;

public class BlockRegistration extends Registration<Block, BlockEntry<? extends Block>> implements DataGenHolder {

    @Nullable private ItemRegistration itemRegistration;
    @Nullable private DataGenManager dataGenManager;

    BlockRegistration(String namespace, Registry<Block> registry) {
        super(namespace, registry);
    }

    @Override
    protected BlockEntry<? extends Block> createEntry(ResourceLocation id, Supplier<? extends Block> supplier) {
        return new BlockEntry<>(getRegistry(), id, AlmostUtils.cast(supplier));
    }

    public BlockRegistration itemRegistration(ItemRegistration itemRegistration) {
        this.itemRegistration = itemRegistration;
        return this;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T extends Block> BlockEntry<T> register(String id, Supplier<? extends T> supplier) {
        return (BlockEntry<T>) super.register(id, supplier);
    }

    @SuppressWarnings("unchecked")
    public <T extends Block> BlockEntry<T> register(String id, String englishName, Supplier<? extends T> supplier) {
        var block = register(id, supplier);
        if (dataGenManager != null) {
            dataGenManager.common().lang(p -> p.addLang(block.get().getDescriptionId(), englishName));
        }
        return (BlockEntry<T>) block;
    }

    public <B extends Block> Builder<B> builder(String id, Material material, Function<BlockBehaviour.Properties, ? extends B> factory) {
        return new Builder<>(id, BlockBehaviour.Properties.of(material), factory);
    }

    public Builder<Block> builder(String id, Material material) {
        return builder(id, material, Block::new);
    }

    public Builder<Block> oreBuilder(String id, Material material, UniformInt xp) {
        return builder(id, material, properties -> new DropExperienceBlock(properties, xp));
    }

    public BlockRegistration dataGen(DataGenManager dataGenManager) {
        this.dataGenManager = dataGenManager;
        return this;
    }

    @Override
    @Nullable
    public DataGenManager getDataGenManager() {
        return dataGenManager;
    }

    @SuppressWarnings("unused")
    public class Builder<B extends Block> {

        private final String id;
        private BlockBehaviour.Properties properties;
        Function<BlockBehaviour.Properties, ? extends B> factory;
        @Nullable protected BiConsumer<BlockEntry<B>, BlockStateProvider> blockstateGeneratorCallback;
        @Nullable protected BiConsumer<BlockEntry<B>, LootTableProvider> lootTableCallback;
        private final Set<TagKey<Block>> blockTags = new HashSet<>();
        /* Item Stuff */
        @Nullable Consumer<ItemRegistration.Builder<? extends BlockItem>> itemBuilderConsumer;
        @Nullable BiFunction<B, Item.Properties, ? extends BlockItem> blockItemFactory;
        private final Set<TagKey<Item>> itemTags = new HashSet<>();
        @Nullable String defaultLang;

        public Builder(String id, BlockBehaviour.Properties properties, Function<BlockBehaviour.Properties, ? extends B> factory) {
            this.id = id;
            this.properties = properties;
            this.factory = factory;
        }

        public Builder<B> item(Consumer<ItemRegistration.Builder<? extends BlockItem>> consumer) {
            itemBuilderConsumer = consumer;
            blockItemFactory = BlockItem::new;
            return this;
        }

        public <I extends BlockItem> Builder<B> item(Consumer<ItemRegistration.Builder<I>> consumer, BiFunction<B, Item.Properties, I> itemFactory) {
            itemBuilderConsumer = AlmostUtils.cast(consumer);
            blockItemFactory = itemFactory;
            return this;
        }

        public Builder<B> defaultItem() {
            return item(builder -> {});
        }

        public Builder<B> defaultItem(CreativeModeTab tab) {
            return item(builder -> builder.creativeTab(tab));
        }

        public Builder<B> defaultItem(Item.Properties properties) {
            return item(builder -> builder.properties(() -> properties));
        }

        public Builder<B> blockState(BiConsumer<BlockEntry<B>, BlockStateProvider> callback) {
            blockstateGeneratorCallback = callback;
            return this;
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

        public Builder<B> noLootTable() {
            properties.noLootTable();
            return this;
        }

        public Builder<B> requiresCorrectToolForDrops() {
            properties.requiresCorrectToolForDrops();
            return this;
        }

        public Builder<B> requiresCorrectToolForDrops(Tiers toolTier) {
            blockTags(switch (toolTier) {
                case STONE -> BlockTags.NEEDS_STONE_TOOL;
                case IRON -> BlockTags.NEEDS_IRON_TOOL;
                case DIAMOND -> BlockTags.NEEDS_DIAMOND_TOOL;
                default -> throw new IllegalStateException("Unexpected value: " + toolTier);
            });
            return requiresCorrectToolForDrops();
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

        public Builder<B> dropOther(Supplier<ItemLike> supplier) {
            checkLootTwice();
            this.lootTableCallback = (entry, provider) -> provider.dropOther(entry.get(), supplier.get());
            return this;
        }

        public Builder<B> dropSelf() {
            checkLootTwice();
            this.lootTableCallback = (entry, provider) -> provider.dropSelf(entry.get());
            return this;
        }

        public Builder<B> loot(BiConsumer<BlockEntry<B>, LootTable.Builder> callback) {
            checkLootTwice();
            this.lootTableCallback = (entry, provider) -> {
                LootTable.Builder builder = LootTable.lootTable();
                callback.accept(entry, builder);
                provider.add(entry.get(), builder);
            };
            return this;
        }

        public Builder<B> loot(Function<BlockEntry<B>, LootTable.Builder> factory) {
            checkLootTwice();
            this.lootTableCallback = (entry, provider) -> {
                LootTable.Builder builder = factory.apply(entry);
                provider.add(entry.get(), builder);
            };
            return this;
        }

        @SafeVarargs
        public final Builder<B> blockTags(TagKey<Block>... tags) {
            blockTags.addAll(Arrays.asList(tags));
            return this;
        }

        @SafeVarargs
        public final Builder<B> itemTags(TagKey<Item>... tags) {
            itemTags.addAll(Arrays.asList(tags));
            return this;
        }

        public Builder<B> defaultLang(String englishName) {
            defaultLang = englishName;
            return this;
        }

        public Builder<B> effectiveTools(ToolType... tools) {
            blockTags(AlmostUtils.cast(Arrays.stream(tools).map(t -> t.mineableBlockTag).toArray(TagKey[]::new)));
            return this;
        }

        private String generateDefaultLang() {
            return Objects.requireNonNullElseGet(defaultLang, () -> AlmostUtils.capitalizeWords(id.replace('_', ' ')));
        }

        public BlockEntry<B> register() {
            BlockEntry<B> block = BlockRegistration.this.register(id, generateDefaultLang(), () -> factory.apply(properties));

            var ir = BlockRegistration.this.itemRegistration;
            if (ir == null && itemBuilderConsumer != null) {
                throw new IllegalStateException(
                    "Cannot register item for block " + block.getId() + " without an item registration");
            }

            if (itemBuilderConsumer != null && blockItemFactory != null) {
                var itemBuilder = ir.builder(id, props -> blockItemFactory.apply(block.get(), props)).noLang();
                itemTags.forEach(itemBuilder::tags);
                itemBuilder.model((e, provider) -> provider.getModelConsumer().accept(
                    ModelLocationUtils.getModelLocation(e.get()), new DelegatedModel(ModelLocationUtils.getModelLocation(block.get()))
                ));
                itemBuilderConsumer.accept(itemBuilder);
                itemBuilder.register();
            }

            applyDataGen(dg -> {
                if (blockstateGeneratorCallback != null) {
                    dg.common().blockState(provider -> blockstateGeneratorCallback.accept(block, provider));
                }
                if (lootTableCallback != null) {
                    dg.common().loot(provider -> lootTableCallback.accept(block, provider));
                }
                dg.platform().tags(Registry.BLOCK, p -> blockTags.forEach(tag -> p.tag(tag).add(block.get())));
            });

            return block;
        }

        private void checkLootTwice() {
            if (lootTableCallback != null) throw new IllegalArgumentException("Cannot set loot table twice");
        }
    }
}
