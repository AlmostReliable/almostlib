package com.github.almostreliable.lib;

import com.github.almostreliable.lib.api.AlmostLib;
import com.github.almostreliable.lib.api.registry.RegistryManager;
import net.minecraft.world.item.CompassItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.Tiers;

import java.util.function.Supplier;

public class CommonClass {
    // TODO Clean up later, just for testing right now
    public static RegistryManager registry = AlmostLib.INSTANCE.createRegistry(AlmostConstants.MOD_ID);

    public static Supplier<CompassItem> DUMMY_ITEM = registry
            .item("dummy_item", CompassItem::new)
            .durability(3)
            .tab(CreativeModeTab.TAB_COMBAT)
            .register();

    public static Supplier<SwordItem> DUMMY_SWORD = registry
            .item("dummy_sword", properties -> new SwordItem(Tiers.DIAMOND, 10, 2, properties))
            .durability(3)
            .tab(CreativeModeTab.TAB_COMBAT)
            .register();

    public static void init() {
        registry.init();
    }
}
