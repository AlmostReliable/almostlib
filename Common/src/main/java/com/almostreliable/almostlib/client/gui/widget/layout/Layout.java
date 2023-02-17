package com.almostreliable.almostlib.client.gui.widget.layout;

import com.almostreliable.almostlib.client.gui.widget.composite.CompositeWidget;

import javax.annotation.Nullable;

@FunctionalInterface
public interface Layout {

    @Nullable Result calculate(CompositeWidget composite);

    record Result(int getWidth, int getHeight) {

    }
}
