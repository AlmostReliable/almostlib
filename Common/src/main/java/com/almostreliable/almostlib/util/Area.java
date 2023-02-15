package com.almostreliable.almostlib.util;

public interface Area {

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
