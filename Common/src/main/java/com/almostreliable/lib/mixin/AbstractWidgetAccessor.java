package com.almostreliable.lib.mixin;

import net.minecraft.client.gui.components.AbstractWidget;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(AbstractWidget.class)
public interface AbstractWidgetAccessor {

    @Accessor("alpha")
    float getAlpha();

    @Accessor("isHovered")
    boolean isHovered();

    @Accessor("height")
    void setHeight(int height);
}
