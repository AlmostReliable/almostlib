package com.almostreliable.almostlib.item;

import com.almostreliable.almostlib.AlmostLib;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import javax.annotation.Nullable;

public class LazyCreativeTab {

    private final ResourceLocation id;
    @Nullable private ItemStack lazyCreativeTabItem;

    private final CreativeModeTab tab;

    public LazyCreativeTab(ResourceLocation id) {
        this.id = id;
        this.tab = AlmostLib.PLATFORM.createCreativeTab(id, this::getCreativeTabItem);
    }

    public void setItem(ItemStack item) {
        this.lazyCreativeTabItem = item;
    }

    public boolean hasItem() {
        return lazyCreativeTabItem != null;
    }

    private ItemStack getCreativeTabItem() {
        if (lazyCreativeTabItem == null) {
            AlmostLib.LOGGER.error("LazyCreativeTab {} was not initialized!", id);
            return new ItemStack(Items.BARRIER);
        }
        return lazyCreativeTabItem;
    }

    public CreativeModeTab getTab() {
        return tab;
    }
}
