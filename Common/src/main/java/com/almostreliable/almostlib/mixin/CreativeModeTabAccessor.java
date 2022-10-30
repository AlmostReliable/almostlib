package com.almostreliable.almostlib.mixin;

import net.minecraft.world.item.CreativeModeTab;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(CreativeModeTab.class)
public interface CreativeModeTabAccessor {
    @Accessor("TABS")
    static void setTabs(CreativeModeTab[] tabs) {
        throw new AssertionError();
    }
}
