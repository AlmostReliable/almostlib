package com.almostreliable.almostlib.client.gui;

import com.almostreliable.almostlib.client.gui.widget.composite.CompositeWidget;
import com.almostreliable.almostlib.util.Area;
import com.google.common.base.Preconditions;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

import javax.annotation.Nullable;

/**
 * Implemented to store data for a widget.
 */
@Environment(EnvType.CLIENT)
public interface WidgetData extends Area.Mutable {

    static WidgetData of(int x, int y, int width, int height) {
        return new Simple(x, y, width, height);
    }

    /**
     * Restore the origin area of the widget.
     */
    default void restoreOrigin() {
        applyArea(getOriginArea());
    }

    /**
     * Apply custom position and dimensions to the widget.
     *
     * @param area The area to apply.
     */
    default void applyArea(Area area) {
        setX(area.getX());
        setY(area.getY());
        setWidth(area.getWidth());
        setHeight(area.getHeight());
    }

    /**
     * Get the origin area of the widget. This is the area that the widget was created with.
     *
     * @return The origin area of the widget.
     */
    Area getOriginArea();

    boolean isActive();

    void setActive(boolean active);

    boolean isVisible();

    void setVisible(boolean visible);

    float getAlpha();

    void setAlpha(float alpha);

    /**
     * Checks if the widget is hovered.
     * <p>
     * To match the hovered state of the widget, the mouse must be within
     * the bounds of the widget, and it should be visible.
     *
     * @return True when the widget is hovered, false otherwise.
     */
    boolean isHovered();

    void setHovered(boolean hovered);

    @Nullable
    WidgetChangeListener getParent();

    /**
     * Sets the parent of the widget to notify on certain events.
     * <p>
     * This is mainly used by {@link CompositeWidget} to notify the parent of a resize.
     *
     * @param parent The parent to notify.
     */
    void setParent(@Nullable WidgetChangeListener parent);

    class Simple implements WidgetData {

        private final Area originArea;
        private int x;
        private int y;
        private int width;
        private int height;
        private boolean active;
        private boolean visible;
        private float alpha;
        private boolean hovered;
        @Nullable private WidgetChangeListener parent;

        public Simple(int x, int y, int width, int height) {
            this.x = x;
            this.y = y;
            this.width = width;
            this.height = height;
            this.active = true;
            this.visible = true;
            this.alpha = 1.0f;
            this.hovered = false;
            this.originArea = Area.of(x, y, width, height);
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
        public Area getOriginArea() {
            return originArea;
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
