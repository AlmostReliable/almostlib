package com.github.almostreliable.testmod;

import com.github.almostreliable.lib.AlmostLib;
import com.github.almostreliable.lib.registry.AlmostManager;
import com.github.almostreliable.lib.registry.RegistryEntry;
import com.github.almostreliable.testmod.items.ToolItems;
import net.minecraft.core.Registry;
import net.minecraft.data.models.blockstates.MultiVariantGenerator;
import net.minecraft.data.models.blockstates.Variant;
import net.minecraft.data.models.blockstates.VariantProperties;
import net.minecraft.data.models.model.ModelTemplates;
import net.minecraft.data.models.model.TextureMapping;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.*;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.material.Material;

public class TestModCommon {
    public static final String MOD_ID = "almostreliable_testmod";
    public static final AlmostManager MANAGER = AlmostLib.INSTANCE.createManager(MOD_ID);
    public static final CreativeModeTab TAB = MANAGER.tab("items", "Test Items", () -> new ItemStack(Blocks.ANVIL));

    public static final RegistryEntry<CompassItem> DUMMY_ITEM = MANAGER
            .item("dummy_item", CompassItem::new)
            .durability(3)
            .tab(TAB)
            .defaultModel()
            .register();

    public static final RegistryEntry<SwordItem> DUMMY_SWORD = MANAGER
            .itemSword("dummy_sword", Tiers.GOLD, 3, 1, SwordItem::new)
            .durability(3)
            .tab(TAB)
            .defaultLang()
            .defaultHandheldModel()
            .register();

    public static final RegistryEntry<ToolItems.ShovelItem> DUMMY_SHOVEL = MANAGER
            .itemTool("dummy_shovel", Tiers.GOLD, 3, 1, ToolItems.ShovelItem::new)
            .defaultHandheldModel()
            .defaultLang()
            .tab(TAB)
            .register();

    public static final RegistryEntry<ToolItems.AxeItem> DUMMY_AXE = MANAGER
            .itemTool("dummy_axe", Tiers.GOLD, 3, 1, ToolItems.AxeItem::new)
            .defaultHandheldModel()
            .defaultLang()
            .tab(TAB)
            .register();

    public static final RegistryEntry<ToolItems.HoeItem> DUMMY_HOE = MANAGER
            .itemTool("dummy_hoe", Tiers.GOLD, 3, 1, ToolItems.HoeItem::new)
            .defaultHandheldModel()
            .defaultLang()
            .tab(TAB)
            .register();

    public static final RegistryEntry<ToolItems.PickaxeItem> DUMMY_PICKAXE = MANAGER
            .itemTool("dummy_pickaxe", Tiers.NETHERITE, 3, 1, ToolItems.PickaxeItem::new)
            .defaultHandheldModel()
            .defaultLang()
            .tab(TAB)
            .register();

    public static final RegistryEntry<TestBlock> TEST_BLOCK = MANAGER
            .block("test_block", Material.BAMBOO, TestBlock::new)
            .strength(300)
            .noDrops()
            .blockstate((entry, provider) -> {
                ResourceLocation location = ModelTemplates.CUBE_ALL.create(entry.get(),
                        TextureMapping.cube(entry.get()),
                        provider.getModelConsumer());
                MultiVariantGenerator generator = MultiVariantGenerator.multiVariant(entry.get(),
                        Variant.variant().with(VariantProperties.MODEL, location));
                provider.addBlockState(generator);
            })
            .tab(TAB)
            .defaultLang()
            .register();

    public static final RegistryEntry<BlockItem> TEST_BLOCK_ITEM = MANAGER.getLink(Registry.ITEM_REGISTRY, TEST_BLOCK);

    public static final RegistryEntry<MenuType<TestMenu>> TEST_MENU = MANAGER.menu("test_menu",
            (id, inventory, buffer) -> new TestMenu(id),
            () -> TestScreen::new);

    public static final RegistryEntry<BlockEntityType<TestBlockEntity>> TEST_BLOCK_ENTITY = MANAGER
            .blockEntity("test_block_entity", TestBlockEntity::new)
            .blocks(block -> block == TEST_BLOCK.get())
            .renderer(() -> TestBlockEntityRenderer::new)
            .register();
}
