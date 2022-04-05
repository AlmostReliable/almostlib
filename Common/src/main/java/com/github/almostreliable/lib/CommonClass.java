package com.github.almostreliable.lib;

import com.github.almostreliable.lib.api.AlmostLib;
import com.github.almostreliable.lib.api.registry.RegistryManager;
import net.minecraft.world.item.*;

import java.util.function.Supplier;

public class CommonClass {
    // TODO Clean up later, just for testing right now
    public static RegistryManager registry = AlmostLib.INSTANCE.createRegistry(AlmostConstants.MOD_ID);

    Supplier<CompassItem> DUMMY_ITEM = registry
            .item("dummy_item", CompassItem::new)
            .durability(3)
            .tab(CreativeModeTab.TAB_COMBAT)
            .register();

    Supplier<SwordItem> DUMMY_SWORD = registry
            .item("dummy_item", properties -> new SwordItem(Tiers.DIAMOND, 10, 2, properties))
            .durability(3)
            .tab(CreativeModeTab.TAB_COMBAT)
            .register();

    public static void init() {
        registry.init();
    }
}
