package com.almostreliable.lib.client.gui.widget;

import com.almostreliable.lib.client.gui.WidgetData;
import com.almostreliable.lib.client.gui.widget.composite.CompositeWidget;
import com.mojang.blaze3d.vertex.PoseStack;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.components.Widget;
import net.minecraft.resources.ResourceLocation;

import javax.annotation.OverridingMethodsMustInvokeSuper;

/**
 * Implemented by all kinds of widgets holding {@link WidgetData} in
 * order to add convenience functionality.
 */
@Environment(EnvType.CLIENT)
public interface AlmostWidget<T extends WidgetData> extends Widget {

    static ResourceLocation getTexture(String namespace, String... path) {
        return new ResourceLocation(namespace, "textures/" + String.join("/", path) + ".png");
    }

    @OverridingMethodsMustInvokeSuper
    @Override
    default void render(PoseStack poseStack, int mouseX, int mouseY, float delta) {
        if (!getData().isActive() || !getData().isVisible()) return; // TODO: maybe render grayed out when inactive
        updateHovered(mouseX, mouseY);
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

    /**
     * Sets the visibility of the widget.<br>
     * After the visibility is changed, the parent will be notified of the event.
     * <p>
     * When a widget is invisible, it will not be rendered and will not receive mouse events.
     * <p>
     * Because of the automated notification, this is the recommended way to change the visibility of a widget.
     *
     * @param visible True if the widget should be visible, false otherwise.
     */
    default void setVisible(boolean visible) {
        boolean wasVisible = getData().isVisible();
        getData().setVisible(visible);
        if (wasVisible != getData().isVisible() && getData().getParent() != null) {
            getData().getParent().onWidgetResize(this);
        }
    }

    /**
     * Updates the hovered state of the widget. Widgets may call this in their {@link #render(PoseStack, int, int, float)} method.
     *
     * @param mouseX The x coordinate of the mouse.
     * @param mouseY The y coordinate of the mouse.
     */
    default void updateHovered(int mouseX, int mouseY) {
        getData().setHovered(getData().inBounds(mouseX, mouseY));
    }

    T getData();
}
