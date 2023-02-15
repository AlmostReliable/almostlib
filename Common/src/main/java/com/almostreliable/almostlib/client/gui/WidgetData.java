package com.almostreliable.almostlib.client.gui;

import com.almostreliable.almostlib.util.Area;
import com.google.common.base.Preconditions;

import javax.annotation.Nullable;

/**
 * A simple interface to store the data for a widget.
 */
public interface WidgetData extends Area.Mutable {

    static WidgetData of(int x, int y, int width, int height) {
        return new Simple(x, y, width, height);
    }

    void setX(int x);

    void setY(int y);

    /**
     * Set the width and height of the area. Implementations must ensure that the values are always positive
     *
     * @param height the height of the area
     */
    void setHeight(int height);

    void setActive(boolean active);

    boolean isActive();

    void setVisible(boolean visible);

    boolean isVisible();

    void setAlpha(float alpha);

    float getAlpha();

    void setHovered(boolean hovered);

    boolean isHovered();

    void setParent(@Nullable WidgetChangeListener parent);

    @Nullable
    WidgetChangeListener getParent();

    class Simple implements WidgetData {

        private int x;
        private int y;
        private int width;
        private int height;
        private boolean active;
        private boolean visible;
        private float alpha;
        private boolean hovered;
        private WidgetChangeListener parent;

        public Simple(int x, int y, int width, int height) {
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
        public int getWidth() {
            return width;
        }

        @Override
        public void setWidth(int width) {
            Preconditions.checkArgument(width >= 0, "Width must be positive");
            this.width = width;
        }

        @Override
        public int getHeight() {
            return height;
        }

        @Override
        public void setHeight(int height) {
            Preconditions.checkArgument(height >= 0, "Height must be positive");
            this.height = height;
        }

        @Override
        public void setActive(boolean active) {
            this.active = active;
        }

        @Override
        public boolean isActive() {
            return active;
        }

        @Override
        public void setVisible(boolean visible) {
            this.visible = visible;
        }

        @Override
        public boolean isVisible() {
            return visible;
        }

        @Override
        public void setAlpha(float alpha) {
            this.alpha = alpha;
        }

        @Override
        public float getAlpha() {
            return alpha;
        }

        @Override
        public void setHovered(boolean hovered) {
            this.hovered = hovered;
        }

        /**
         * Check if the widget is hovered. To match the hovered state of the widget,
         * the mouse must be within the bounds of the widget, and it should be visible.
         *
         * @return true if the widget is hovered.
         */
        @Override
        public boolean isHovered() {
            return hovered && visible;
        }

        @Override
        public void setParent(@Nullable WidgetChangeListener parent) {
            this.parent = parent;
        }

        @Nullable
        @Override
        public WidgetChangeListener getParent() {
            return parent;
        }
    }
}
