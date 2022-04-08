package com.github.almostreliable.lib.registry;

import com.github.almostreliable.lib.registry.builders.BlockBuilder;
import com.github.almostreliable.lib.registry.builders.ItemBuilder;
import com.mojang.datafixers.util.Function4;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.*;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.material.MaterialColor;

import javax.annotation.Nullable;
import java.util.function.Function;
import java.util.function.Supplier;

public interface RegistryManager {
    String getNamespace();

    <I extends SwordItem> ItemBuilder<I> itemSword(String id, Tier tier, int atkDamage, int atkSpeed, Function4<Tier, Integer, Integer, Item.Properties, I> factory);

    <I extends Item> ItemBuilder<I> item(String id, Function<Item.Properties, I> factory);

    <B extends Block, I extends BlockItem> BlockBuilder<B, I> block(String id, Material material, Function<BlockBehaviour.Properties, B> factory);

    <B extends Block, I extends BlockItem> BlockBuilder<B, I> block(String id, Material material, MaterialColor color, Function<BlockBehaviour.Properties, B> factory);

    <B extends Block, I extends BlockItem> BlockBuilder<B, I> block(String id, Material material, DyeColor color, Function<BlockBehaviour.Properties, B> factory);

    <B extends Block, I extends BlockItem> BlockBuilder<B, I> block(String id, BlockBehaviour.Properties properties, Function<BlockBehaviour.Properties, B> factory);

    @Nullable
    <E> Supplier<E> getEntry(ResourceKey<Registry<E>> resourceKey, String id);

    void init();
}
