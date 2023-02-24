package com.almostreliable.almostlib.client.gui;

import com.almostreliable.almostlib.client.gui.widget.AlmostWidget;
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
    void onWidgetResize(AlmostWidget<?> widget);
}
