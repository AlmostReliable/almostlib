package com.almostreliable.lib.client.gui;

import com.almostreliable.lib.client.gui.widget.AlmostWidget;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

/**
 * Implemented by widgets that can listen to events of child widgets.
 */
@Environment(EnvType.CLIENT)
public interface WidgetChangeListener {

    /**
     * Called when a child widget is resized.
     *
     * @param widget The widget that was resized.
     */
    default void onWidgetResize(AlmostWidget<?> widget) {}

    /**
     * Called when a child widget's visibility is changed.
     *
     * @param widget The widget that had its visibility changed.
     */
    default void onWidgetVisibilityChanged(AlmostWidget<?> widget) {}
}
