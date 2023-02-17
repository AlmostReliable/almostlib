package com.almostreliable.almostlib.util;

public interface Area {

    static Area of(int x, int y, int width, int height) {
        return new Simple(x, y, width, height);
    }

    int getX();

    int getY();

    /**
     * Returns the width of the area. Implementations must ensure that the width is always positive
     *
     * @return the width of the area
     */
    int getWidth();

    /**
     * Returns the height of the area. Implementations must ensure that the height is always positive
     *
     * @return the height of the area
     */
    int getHeight();

    default int getRight() {
        return getX() + getWidth();
    }

    default int getBottom() {
        return getY() + getHeight();
    }

    default boolean isVerticalInside(double v) {
        return getY() <= v && v < getBottom();
    }

    default boolean isHorizontalInside(double h) {
        return getX() <= h && h < getRight();
    }

    default boolean inBounds(double x, double y) {
        return isHorizontalInside(x) && isVerticalInside(y);
    }

    default Area shrink(int amount) {
        return expand(-amount, -amount);
    }

    default Area shrink(int x, int y) {
        return expand(-x, -y);
    }

    default Area expand(int amount) {
        return expand(amount, amount);
    }

    default Area expand(int x, int y) {
        return new Simple(getX() - x, getY() - y, getWidth() + 2 * x, getHeight() + 2 * y);
    }

    default Area intersect(Area other) {
        int x = Math.max(getX(), other.getX());
        int y = Math.max(getY(), other.getY());
        int right = Math.min(getRight(), other.getRight());
        int bottom = Math.min(getBottom(), other.getBottom());
        return new Simple(x, y, right - x, bottom - y);
    }

    default Area move(int x, int y) {
        return new Simple(getX() + x, getY() + y, getWidth(), getHeight());
    }

    interface Mutable extends Area {

        void setX(int x);

        void setY(int y);

        /**
         * Set the width of the area. Implementations must ensure that the width is always positive
         *
         * @param width the width of the area
         */
        void setWidth(int width);

        /**
         * Set the height of the area. Implementations must ensure that the height is always positive
         *
         * @param height the height of the area
         */
        void setHeight(int height);
    }

    record Simple(int getX, int getY, int getWidth, int getHeight) implements Area {

        public Simple {
            if (getWidth < 0) {
                throw new IllegalArgumentException("Width must be positive");
            }
            if (getHeight < 0) {
                throw new IllegalArgumentException("Height must be positive");
            }
        }
    }
}
