package com.almostreliable.almostlib.registry;

import com.almostreliable.almostlib.util.AlmostUtils;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;

import java.util.function.Supplier;

public class MenuEntry<M extends AbstractContainerMenu> extends RegistryEntryImpl<MenuType<M>> {

    public MenuEntry(ResourceLocation id, Supplier<MenuType<M>> supplier) {
        super(AlmostUtils.cast(Registry.MENU), id, supplier);
    }
}
