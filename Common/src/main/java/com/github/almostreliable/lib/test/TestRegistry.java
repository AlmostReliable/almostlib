package com.github.almostreliable.lib.test;

import com.github.almostreliable.lib.AlmostLib;
import com.github.almostreliable.lib.registry.RegistryEntry;
import com.github.almostreliable.lib.registry.RegistryManager;
import net.minecraft.core.Registry;
import net.minecraft.world.item.*;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BannerBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.material.Material;

public class TestRegistry {
    public static RegistryManager REGISTRY;
    public static RegistryEntry<CompassItem> DUMMY_ITEM;
    public static RegistryEntry<SwordItem> DUMMY_SWORD;
    public static RegistryEntry<BlockItem> TEST_BLOCK_ITEM;
    public static RegistryEntry<BlockEntityType<BannerBlockEntity>> DUMMY_ENTITY;

    public static RegistryEntry<TestBlock> TEST_BLOCK;
    public static RegistryEntry<BlockEntityType<TestBlockEntity>> TEST_BLOCK_ENTITY;

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

        TEST_BLOCK = REGISTRY
                .block("test_block", Material.BAMBOO, TestBlock::new)
                .strength(300)
                .noDrops()
                .noCollision()
                .noOcclusion()
                .register();

        TEST_BLOCK_ITEM = REGISTRY.getLink(Registry.ITEM_REGISTRY, TEST_BLOCK);

        TEST_BLOCK_ENTITY = REGISTRY
                .blockEntity("test_block_entity", TestBlockEntity::new)
                .blocks(() -> new Block[]{ TEST_BLOCK.get() })
                .renderer(TestBlockEntityRenderer::new)
                .register();

//        REGISTRY.registerRenderer(TEST_BLOCK_ENTITY, TestBlockEntityRenderer::new);
//        REGISTRY.registerRenderer(DUMMY_ENTITY, SignRenderer::new);
//        REGISTRY.registerRenderer(DUMMY_ENTITY.get(), SignRenderer::new);
//        REGISTRY.registerRenderer(BlockEntityType.SIGN, SignRenderer::new);
    }
}
