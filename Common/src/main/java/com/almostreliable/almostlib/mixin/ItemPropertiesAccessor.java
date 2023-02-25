package com.almostreliable.almostlib.mixin;

import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import javax.annotation.Nullable;

@Mixin(Item.Properties.class)
public interface ItemPropertiesAccessor {

    @Nullable
    @Accessor("category")
    CreativeModeTab getCreativeTab();
}
