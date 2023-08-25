package com.almostreliable.lib.client.rendering;

public interface Scalable<T> {

    T scaleAll(float x, float y, float z);

    default T scale(float x, float y) {
        return scaleAll(x, y, 1);
    }

    default T scaleX(float x) {
        return scaleAll(x, 1, 1);
    }

    default T scaleY(float y) {
        return scaleAll(1, y, 1);
    }

    default T scaleZ(float z) {
        return scaleAll(1, 1, z);
    }
}
