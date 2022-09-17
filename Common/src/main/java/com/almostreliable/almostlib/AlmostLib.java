package com.almostreliable.almostlib;

import com.almostreliable.almostlib.registry.ItemEntry;
import com.almostreliable.almostlib.registry.ItemRegistration;
import com.almostreliable.almostlib.registry.Registration;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ServiceLoader;

public class AlmostLib {
    public static final Logger LOGGER = LogManager.getLogger(BuildConfig.MOD_NAME);
    public static final AlmostLibPlatform PLATFORM = loadService(AlmostLibPlatform.class);

    public static final ItemRegistration ITEMS = Registration.items(BuildConfig.MOD_ID);

    public static final ItemEntry<Item> TEST_ITEM = ITEMS.register("test_item",
            () -> new Item(new Item.Properties().tab(
                    CreativeModeTab.TAB_COMBAT)));

    public static <T> T loadService(Class<T> clazz) {
        return ServiceLoader
                .load(clazz)
                .findFirst()
                .orElseThrow(() -> new NullPointerException("Failed to load service for class: " + clazz.getName()));
    }
}
