package com.almostreliable.almostlib.client.gui.widget.composite;

import com.almostreliable.almostlib.client.gui.Padding;
import com.almostreliable.almostlib.client.gui.widget.AlmostWidget;

/**
 * A widget that stacks other widgets on top of each other.
 */
public class StackPanel extends CompositeWidget {

    private boolean fullWidth = false;
    private Padding padding = Padding.ZERO;

    public StackPanel(int x, int y, int width) {
        super(x, y, width, 0);
    }

    public void setPadding(Padding padding) {
        this.padding = padding;
        markForRecalculation();
    }

    public void setAllWidgetsFullWidth(boolean fullWidth) {
        this.fullWidth = fullWidth;
        markForRecalculation();
    }

    @Override
    protected void calculateLayout() {
        int height = padding.top();

        for (AlmostWidget<?> widget : widgets) {
            int widgetWidth = fullWidth ? getData().getWidth() : widget.getData().getWidth();
            widgetWidth -= padding.left() + padding.right();
            int widgetHeight = widget.getData().getHeight();

            widget.resize(widgetWidth, widgetHeight);
            widget.getData().setX(padding.left());
            widget.getData().setY(height);

            height += widgetHeight + getSpacing();
        }

        height -= getSpacing(); // Remove the last spacing. We don't want to add spacing after the last widget
        height += padding.bottom();
        resize(getData().getWidth(), height);
    }
}
