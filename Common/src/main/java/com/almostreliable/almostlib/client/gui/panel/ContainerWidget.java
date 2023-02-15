package com.almostreliable.almostlib.client.gui.panel;

import com.almostreliable.almostlib.client.gui.AlmostWidget;
import com.almostreliable.almostlib.client.gui.WidgetData;

import java.util.Collection;

public interface ContainerWidget<T extends WidgetData> extends AlmostWidget<T> {

    Collection<AlmostWidget<?>> getChildren();

    void addChild(AlmostWidget<?> child);
}
