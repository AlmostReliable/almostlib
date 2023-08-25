package com.almostreliable.lib.client.rendering;

import com.mojang.math.Vector3f;
import net.minecraft.core.Vec3i;

public interface Translatable<T> {

    T translateAll(double x, double y, double z);

    default T translate(double x, double y) {
        return translateAll(x, y, 0);
    }

    default T translate(Vector3f v) {
        return translateAll(v.x(), v.y(), v.z());
    }

    default T translate(Vec3i pos) {
        return translateAll(pos.getX(), pos.getY(), pos.getZ());
    }

    default T translateX(double x) {
        return translateAll(x, 0, 0);
    }

    default T translateY(double y) {
        return translateAll(0, y, 0);
    }

    default T translateZ(double z) {
        return translateAll(0, 0, z);
    }
}
