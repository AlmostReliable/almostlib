package com.almostreliable.almostlib.client.gui.widget;

import com.almostreliable.almostlib.client.gui.WidgetData;
import com.almostreliable.almostlib.client.gui.widget.composite.CompositeWidget;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.components.Widget;
import net.minecraft.resources.ResourceLocation;

@Environment(EnvType.CLIENT)
public interface AlmostWidget<T extends WidgetData> extends Widget {

    static ResourceLocation getTexture(String namespace, String... path) {
        return new ResourceLocation(namespace, "textures/" + String.join("/", path) + ".png");
    }

    /**
     * Set the width and height of the area. After resize is called, the parent will be notified of the resize.
     * <p>
     * This is the preferred way to resize a widget, as it will notify the parent of the widget.<br>
     * {@link WidgetData#setWidth(int)} and {@link WidgetData#setHeight(int)} should only be used while calculating a new layout. This is mainly used by {@link CompositeWidget}, when layout is calculated.
     *
     * @param width  the width of the area
     * @param height the height of the area
     */
    default void resize(int width, int height) {
        getData().setWidth(width);
        getData().setHeight(height);
        if (getData().getParent() != null) {
            getData().getParent().onWidgetResize(this);
        }
    }

    T getData();
}
