package com.almostreliable.almostlib.client.gui.widget.layout;

import com.almostreliable.almostlib.client.gui.Padding;
import com.almostreliable.almostlib.client.gui.WidgetData;
import com.almostreliable.almostlib.client.gui.widget.AlmostWidget;

public interface Layouts {

    Layout NOTHING = composite -> null;

    Layout VERTICAL_STACK = composite -> {
        Padding padding = composite.getPadding();
        WidgetData data = composite.getData();

        int areaHeight = 0;
        int areaWidth = data.getWidth() - padding.left() - padding.right();

        for (AlmostWidget<?> widget : composite.getWidgets()) {
            widget.getData().restoreOrigin();
            int widgetWidth = widget.getData().getWidth();
            if (composite.isFullWidthWidgets()) {
                widgetWidth = areaWidth;
            }
            int widgetHeight = widget.getData().getHeight();

            widget.getData().setWidth(widgetWidth);
            widget.getData().setHeight(widgetHeight);
            widget.getData().setX(data.getX() + padding.left());
            widget.getData().setY(data.getY() + areaHeight + padding.top());

            areaHeight += widgetHeight + composite.getVerticalSpacing();
        }
        areaHeight -= composite.getVerticalSpacing(); // Remove the last spacing. We don't want to add spacing after the last widget
        return new Layout.Result(areaWidth, areaHeight);
    };
}
