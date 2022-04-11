package com.github.almostreliable.lib.test;

import com.github.almostreliable.lib.AlmostLib;
import com.github.almostreliable.lib.registry.RegistryEntry;
import com.github.almostreliable.lib.registry.RegistryManager;
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
    public static RegistryManager REGISTRY;
    public static RegistryEntry<CompassItem> DUMMY_ITEM;
    public static RegistryEntry<SwordItem> DUMMY_SWORD;
    public static RegistryEntry<BlockItem> TEST_BLOCK_ITEM;
    public static RegistryEntry<BlockEntityType<BannerBlockEntity>> DUMMY_ENTITY;

    public static RegistryEntry<TestBlock> TEST_BLOCK;
    public static RegistryEntry<BlockEntityType<TestBlockEntity>> TEST_BLOCK_ENTITY;
    public static RegistryEntry<MenuType<TestMenu>> TEST_MENU;

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
                .blockstate((entry, provider) -> {
                    ResourceLocation location = ModelTemplates.CUBE_ALL.create(entry.get(),
                            TextureMapping.cube(entry.get()),
                            provider.getModelConsumer());
                    MultiVariantGenerator generator = MultiVariantGenerator.multiVariant(entry.get(),
                            Variant.variant().with(
                                    VariantProperties.MODEL, location));
                    provider.addBlockState(generator);
                })
                .register();

        TEST_BLOCK_ITEM = REGISTRY.getLink(Registry.ITEM_REGISTRY, TEST_BLOCK);

        TEST_BLOCK_ENTITY = REGISTRY
                .blockEntity("test_block_entity", TestBlockEntity::new)
                .blocks(block -> block == TEST_BLOCK.get())
                .renderer(TestBlockEntityRenderer::new)
                .register();

        TEST_MENU = REGISTRY.menu("test_menu", (id, inventory, buffer) -> new TestMenu(id), TestScreen::new);
// return MultiVariantGenerator.multiVariant($$0).with(PropertyDispatch.property(BlockStateProperties.POWERED).select(false, Variant.variant().with(VariantProperties.MODEL, $$1)).select(true, Variant.variant().with(VariantProperties.MODEL, $$2))).with(PropertyDispatch.properties(BlockStateProperties.ATTACH_FACE, BlockStateProperties.HORIZONTAL_FACING).select(AttachFace.FLOOR, Direction.EAST, Variant.variant().with(VariantProperties.Y_ROT, VariantProperties.Rotation.R90)).select(AttachFace.FLOOR, Direction.WEST, Variant.variant().with(VariantProperties.Y_ROT, VariantProperties.Rotation.R270)).select(AttachFace.FLOOR, Direction.SOUTH, Variant.variant().with(VariantProperties.Y_ROT, VariantProperties.Rotation.R180)).select(AttachFace.FLOOR, Direction.NORTH, Variant.variant()).select(AttachFace.WALL, Direction.EAST, Variant.variant().with(VariantProperties.Y_ROT, VariantProperties.Rotation.R90).with(VariantProperties.X_ROT, VariantProperties.Rotation.R90).with(VariantProperties.UV_LOCK, true)).select(AttachFace.WALL, Direction.WEST, Variant.variant().with(VariantProperties.Y_ROT, VariantProperties.Rotation.R270).with(VariantProperties.X_ROT, VariantProperties.Rotation.R90).with(VariantProperties.UV_LOCK, true)).select(AttachFace.WALL, Direction.SOUTH, Variant.variant().with(VariantProperties.Y_ROT, VariantProperties.Rotation.R180).with(VariantProperties.X_ROT, VariantProperties.Rotation.R90).with(VariantProperties.UV_LOCK, true)).select(AttachFace.WALL, Direction.NORTH, Variant.variant().with(VariantProperties.X_ROT, VariantProperties.Rotation.R90).with(VariantProperties.UV_LOCK, true)).select(AttachFace.CEILING, Direction.EAST, Variant.variant().with(VariantProperties.Y_ROT, VariantProperties.Rotation.R270).with(VariantProperties.X_ROT, VariantProperties.Rotation.R180)).select(AttachFace.CEILING, Direction.WEST, Variant.variant().with(VariantProperties.Y_ROT, VariantProperties.Rotation.R90).with(VariantProperties.X_ROT, VariantProperties.Rotation.R180)).select(AttachFace.CEILING, Direction.SOUTH, Variant.variant().with(VariantProperties.X_ROT, VariantProperties.Rotation.R180)).select(AttachFace.CEILING, Direction.NORTH, Variant.variant().with(VariantProperties.Y_ROT, VariantProperties.Rotation.R180).with(VariantProperties.X_ROT, VariantProperties.Rotation.R180)));
//
//        REGISTRY.blockstate(blockStateContext -> {
//            ResourceLocation modelLocation = ModelTemplates.CUBE.create(TEST_BLOCK.get(),
//                    TextureMapping.cube(TEST_BLOCK.get()),
//                    blockStateContext.getModelConsumer());
//
//            MultiVariantGenerator generator = MultiVariantGenerator.multiVariant(TEST_BLOCK.get(),
//                    Variant.variant().with(VariantProperties.MODEL, modelLocation));
//            blockStateContext.addBlockState(generator);
//        });
    }
}
