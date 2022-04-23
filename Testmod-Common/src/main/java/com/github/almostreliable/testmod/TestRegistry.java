package com.github.almostreliable.testmod;

import com.github.almostreliable.lib.AlmostLib;
import com.github.almostreliable.lib.registry.AlmostManager;
import com.github.almostreliable.lib.registry.RegistryEntry;
import net.minecraft.core.Registry;
import net.minecraft.data.models.blockstates.MultiVariantGenerator;
import net.minecraft.data.models.blockstates.Variant;
import net.minecraft.data.models.blockstates.VariantProperties;
import net.minecraft.data.models.model.ModelTemplates;
import net.minecraft.data.models.model.TextureMapping;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.*;
import net.minecraft.world.level.block.entity.BannerBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.material.Material;

public class TestRegistry {
//    public static AlmostManager MANAGER;
//    public static RegistryEntry<CompassItem> DUMMY_ITEM;
//    public static RegistryEntry<SwordItem> DUMMY_SWORD;
//    public static RegistryEntry<BlockItem> TEST_BLOCK_ITEM;
//    public static RegistryEntry<BlockEntityType<BannerBlockEntity>> DUMMY_ENTITY;
//
//    public static RegistryEntry<TestBlock> TEST_BLOCK;
//    public static RegistryEntry<BlockEntityType<TestBlockEntity>> TEST_BLOCK_ENTITY;
//    public static RegistryEntry<MenuType<TestMenu>> TEST_MENU;
//
//    public static void init() {
//        MANAGER = AlmostLib.INSTANCE.createManager(AlmostLib.MOD_ID);
//
//        DUMMY_ITEM = MANAGER
//                .item("dummy_item", CompassItem::new)
//                .durability(3)
//                .tab(CreativeModeTab.TAB_COMBAT)
//                .register();
//
//        DUMMY_SWORD = MANAGER
//                .itemSword("dummy_sword", Tiers.GOLD, 3, 1, SwordItem::new)
//                .durability(3)
//                .tab(CreativeModeTab.TAB_COMBAT)
//                .register();
//        TEST_BLOCK = MANAGER
//                .block("test_block", Material.BAMBOO, TestBlock::new)
//                .strength(300)
//                .noDrops()
//                .blockstate((entry, provider) -> {
//                    ResourceLocation location = ModelTemplates.CUBE_ALL.create(entry.get(),
//                            TextureMapping.cube(entry.get()),
//                            provider.getModelConsumer());
//                    MultiVariantGenerator generator = MultiVariantGenerator.multiVariant(entry.get(),
//                            Variant.variant().with(
//                                    VariantProperties.MODEL, location));
//                    provider.addBlockState(generator);
//                })
//                .register();
//
//        TEST_BLOCK_ITEM = MANAGER.getLink(Registry.ITEM_REGISTRY, TEST_BLOCK);
//
//        TEST_BLOCK_ENTITY = MANAGER
//                .blockEntity("test_block_entity", TestBlockEntity::new)
//                .blocks(block -> block == TEST_BLOCK.get())
//                .renderer(() -> TestBlockEntityRenderer::new)
//                .register();
//
//        TEST_MENU = MANAGER.menu("test_menu", (id, inventory, buffer) -> new TestMenu(id), () -> TestScreen::new);
//    }
}
