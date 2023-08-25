package com.almostreliable.lib.client.rendering;

import com.mojang.math.Quaternion;
import com.mojang.math.Vector3f;

public interface Rotatable<T> {

    T rotate(Quaternion rotation);

    default T rotate(float x, float y, float z) {
        return rotate(new Quaternion(x, y, z, true));
    }

    default T rotate(Vector3f degreesV) {
        return rotate(degreesV.x(), degreesV.y(), degreesV.z());
    }

    default T rotateX(float degrees) {
        return rotate(Vector3f.XP.rotationDegrees(degrees));
    }

    default T rotateY(float degrees) {
        return rotate(Vector3f.XP.rotationDegrees(degrees));
    }

    default T rotateZ(float degrees) {
        return rotate(Vector3f.XP.rotationDegrees(degrees));
    }
}
