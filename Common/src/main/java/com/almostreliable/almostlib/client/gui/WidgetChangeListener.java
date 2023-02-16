package com.almostreliable.almostlib.client.gui;

import com.almostreliable.almostlib.client.gui.widget.AlmostWidget;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public interface WidgetChangeListener {

    void onWidgetResize(AlmostWidget<?> widget);
}
