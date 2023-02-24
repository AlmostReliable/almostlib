package com.almostreliable.almostlib.client.gui.widget;

import com.almostreliable.almostlib.client.gui.WidgetData;
import com.almostreliable.almostlib.client.gui.widget.composite.CompositeWidget;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.components.Widget;
import net.minecraft.resources.ResourceLocation;

/**
 * Implemented by all kinds of widgets holding {@link WidgetData} in
 * order to add convenience functionality.
 */
@Environment(EnvType.CLIENT)
public interface AlmostWidget<T extends WidgetData> extends Widget {

    static ResourceLocation getTexture(String namespace, String... path) {
        return new ResourceLocation(namespace, "textures/" + String.join("/", path) + ".png");
    }

    /**
     * Sets the width and height of the widget area.<br>
     * After the resize is performed, the parent will be notified of the event.
     * <p>
     * Because of the automated notification, this is the recommended way to resize a widget.<br>
     * {@link WidgetData#setWidth(int)} and {@link WidgetData#setHeight(int)} should only be used when calculating a new layout.
     * This is mainly used by {@link CompositeWidget} when the layout is calculated.
     *
     * @param width  The width of the area.
     * @param height The height of the area.
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
