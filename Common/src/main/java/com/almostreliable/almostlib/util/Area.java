package com.almostreliable.almostlib.util;

public interface Area {

    static Area of(int x, int y, int width, int height) {
        return new Simple(x, y, width, height);
    }

    default boolean inVerticalBounds(double v) {
        return getY() <= v && v < getBottom();
    }

    default boolean inHorizontalBounds(double h) {
        return getX() <= h && h < getRight();
    }

    default boolean inBounds(double x, double y) {
        return inHorizontalBounds(x) && inVerticalBounds(y);
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

    int getX();

    int getY();

    /**
     * Returns the width of the area. Implementations must ensure that the width is always positive.
     *
     * @return The width of the area.
     */
    int getWidth();

    /**
     * Returns the height of the area. Implementations must ensure that the height is always positive.
     *
     * @return The height of the area.
     */
    int getHeight();

    default int getRight() {
        return getX() + getWidth();
    }

    default int getBottom() {
        return getY() + getHeight();
    }

    interface Mutable extends Area {

        default void setPos(int x, int y) {
            setX(x);
            setY(y);
        }

        default void move(int x, int y) {
            setX(getX() + x);
            setY(getY() + y);
        }

        void setX(int x);

        void setY(int y);

        /**
         * Sets the width of the area. Implementations must ensure that the width is always positive.
         *
         * @param width The width of the area.
         */
        void setWidth(int width);

        /**
         * Sets the height of the area. Implementations must ensure that the height is always positive.
         *
         * @param height The height of the area.
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
