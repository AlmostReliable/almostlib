package com.github.almostreliable.lib.registry.builders;

import com.github.almostreliable.lib.Utils;
import com.github.almostreliable.lib.datagen.BlockStateProvider;
import com.github.almostreliable.lib.datagen.LootTableProvider;
import com.github.almostreliable.lib.datagen.TagsProvider;
import com.github.almostreliable.lib.registry.AlmostManager;
import com.github.almostreliable.lib.registry.RegisterCallback;
import com.github.almostreliable.lib.registry.RegistryEntry;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.MaterialColor;
import net.minecraft.world.level.storage.loot.LootTable;

import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.function.*;

@SuppressWarnings("UnusedReturnValue")
public class BlockBuilder<B extends Block, I extends BlockItem>
        extends AbstractEntryBuilder<B, Block, BlockBuilder<B, I>> {
    protected final Function<BlockBehaviour.Properties, B> factory;
    private final Set<TagKey<Block>> blockTags = new HashSet<>();
    private final Set<TagKey<Item>> itemTags = new HashSet<>();
    protected BlockBehaviour.Properties properties;
    protected boolean noItem;
    protected Consumer<ItemBuilder<I>> itemBuilderConsumer;
    @Nullable
    protected CreativeModeTab creativeTab;
    @Nullable
    protected BiConsumer<RegistryEntry<B>, BlockStateProvider> blockstateGeneratorCallback;
    @Nullable
    protected BiConsumer<B, LootTableProvider> lootTableCallback;

    public BlockBuilder(String id, BlockBehaviour.Properties properties, Function<BlockBehaviour.Properties, B> factory, AlmostManager manager, RegisterCallback registerCallback) {
        super(id, manager, registerCallback);
        this.factory = factory;
        this.properties = properties;
        item(iItemBuilder -> {});
    }

    public BlockBuilder<B, I> noItem() {
        noItem = true;
        return this;
    }

    public BlockBuilder<B, I> item(Consumer<ItemBuilder<I>> item) {
        Objects.requireNonNull(item);
        if (noItem) {
            throw new IllegalArgumentException("noItem() were set. It's not possible to create an item");
        }
        itemBuilderConsumer = item;
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

    public BlockBuilder<B, I> blockstate(BiConsumer<RegistryEntry<B>, BlockStateProvider> callback) {
        this.blockstateGeneratorCallback = callback;
        return this;
    }

    public BlockBuilder<B, I> dropOther(Supplier<ItemLike> supplier) {
        checkLootTwice();
        this.lootTableCallback = (block, provider) -> {
            provider.dropOther(block, supplier.get());
        };
        return this;
    }

    public BlockBuilder<B, I> dropSelf() {
        checkLootTwice();
        this.lootTableCallback = (block, provider) -> {
            provider.dropSelf(block);
        };
        return this;
    }

    public BlockBuilder<B, I> loot(BiConsumer<B, LootTable.Builder> callback) {
        checkLootTwice();
        this.lootTableCallback = (block, provider) -> {
            LootTable.Builder builder = LootTable.lootTable();
            callback.accept(block, builder);
            provider.add(block, builder);
        };
        return this;
    }

    @SafeVarargs
    public final BlockBuilder<B, I> blockTags(TagKey<Block>... tags) {
        blockTags.addAll(Arrays.asList(tags));
        return this;
    }

    @SafeVarargs
    public final BlockBuilder<B, I> itemTags(TagKey<Item>... tags) {
        itemTags.addAll(Arrays.asList(tags));
        return this;
    }

    public BlockBuilder<B, I> defaultLang(String value) {
        return lang(Block::getDescriptionId, $ -> value);
    }

    public BlockBuilder<B, I> defaultLang() {
        return defaultLang(nameToLang());
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
    public void onRegister(RegistryEntry<B> registryEntry) {
        super.onRegister(registryEntry);

        manager.addOnDataGen(dataGenManager -> {
            if (blockstateGeneratorCallback != null) {
                blockstateGeneratorCallback.accept(registryEntry, dataGenManager.getBlockStateProvider());
            }

            if (lootTableCallback != null) {
                lootTableCallback.accept(registryEntry.get(), dataGenManager.getLootTableProvider());
            }

            TagsProvider<Block> tagsProvider = dataGenManager.getTagsProvider(Registry.BLOCK);
            for (TagKey<Block> blockTag : blockTags) {
                tagsProvider.tag(blockTag).add(registryEntry.get());
            }
        });

        if (!noItem) {
            ItemBuilder<I> itemBuilder = new ItemBuilder<I>(name,
                    properties1 -> Utils.cast(new BlockItem(registryEntry.get(), properties1)),
                    manager,
                    registerCallback)
                    .defaultBlockItemModel(registryEntry)
                    .tab(creativeTab == null ? CreativeModeTab.TAB_BUILDING_BLOCKS : creativeTab);
            itemTags.forEach(itemBuilder::tags);
            itemBuilder.register();
        }
    }

    private void checkLootTwice() {
        if (lootTableCallback != null) throw new IllegalArgumentException("Cannot set loot table twice");
    }
}
