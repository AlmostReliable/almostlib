package com.almostreliable.almostlib.client.gui;

import com.almostreliable.almostlib.util.Area;

/**
 * A simple class to store the data for a widget.
 */
public class WidgetData implements Area.Mutable {

    private int x;
    private int y;
    private int width;
    private int height;
    private boolean active;
    private boolean visible;
    private float alpha;
    private boolean hovered;

    public WidgetData(int x, int y, int width, int height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.active = true;
        this.visible = true;
        this.alpha = 1.0f;
        this.hovered = false;
    }

    @Override
    public int getX() {
        return x;
    }

    @Override
    public void setX(int x) {
        this.x = x;
    }

    @Override
    public int getY() {
        return y;
    }

    @Override
    public void setY(int y) {
        this.y = y;
    }

    @Override
    public void setWidth(int width) {
        this.width = width;
    }

    @Override
    public void setHeight(int height) {
        this.height = height;
    }

    @Override
    public int getWidth() {
        return width;
    }

    @Override
    public int getHeight() {
        return height;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public boolean isActive() {
        return active;
    }

    public void setVisible(boolean visible) {
        this.visible = visible;
    }

    public boolean isVisible() {
        return visible;
    }


    public void setAlpha(float alpha) {
        this.alpha = alpha;
    }


    public float getAlpha() {
        return alpha;
    }


    public void setHovered(boolean hovered) {
        this.hovered = hovered;
    }

    /**
     * Check if the widget is hovered. To match the hovered state of the widget,
     * the mouse must be within the bounds of the widget, and it should be visible.
     *
     * @return true if the widget is hovered.
     */

    public boolean isHovered() {
        return hovered && visible;
    }
}
