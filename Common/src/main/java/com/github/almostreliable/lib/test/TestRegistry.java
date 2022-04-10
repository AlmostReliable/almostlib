package com.github.almostreliable.lib.test;

import com.github.almostreliable.lib.AlmostLib;
import com.github.almostreliable.lib.registry.RegistryEntry;
import com.github.almostreliable.lib.registry.RegistryManager;
import net.minecraft.core.Registry;
import net.minecraft.world.item.*;
import net.minecraft.world.level.block.BannerBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BannerBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.material.Material;

public class TestRegistry {
    public static RegistryManager REGISTRY;
    public static RegistryEntry<CompassItem> DUMMY_ITEM;
    public static RegistryEntry<SwordItem> DUMMY_SWORD;
    public static RegistryEntry<Block> DUMMY_BLOCK;
    public static RegistryEntry<BlockItem> DUMMY_BLOCK_LINKED_ITEM;
    public static RegistryEntry<BlockEntityType<BannerBlockEntity>> DUMMY_ENTITY;

    public static void init() {
        REGISTRY = AlmostLib.INSTANCE.createRegistry(AlmostLib.MOD_ID);

        DUMMY_ITEM = REGISTRY
                .item("dummy_item", CompassItem::new)
                .durability(3)
                .tab(CreativeModeTab.TAB_COMBAT)
                .register();

        DUMMY_SWORD = REGISTRY
                .itemSword("dummy_sword", Tiers.GOLD, 3, 1, SwordItem::new)
                .durability(3)
                .tab(CreativeModeTab.TAB_COMBAT)
                .register();

        DUMMY_BLOCK = REGISTRY
                .block("dummy_block", Material.BAMBOO, Block::new)
                .strength(300)
                .noDrops()
                .noCollision()
                .noOcclusion()
                .register();

        DUMMY_BLOCK_LINKED_ITEM = REGISTRY.getLink(Registry.ITEM_REGISTRY, DUMMY_BLOCK);

        DUMMY_ENTITY = REGISTRY
                .blockEntity("dummy_entity", BannerBlockEntity::new)
                .blocks(block -> block instanceof BannerBlock)
                .register();

        REGISTRY.init();
    }
}
