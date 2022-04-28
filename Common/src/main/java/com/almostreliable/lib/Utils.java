package com.almostreliable.lib;

import javax.annotation.Nullable;

public class Utils {
    @SuppressWarnings("unchecked")
    public static <T> T cast(Object o) {
        return (T) o;
    }

    @SuppressWarnings("unchecked")
    @Nullable
    public static <T> T nullableCast(@Nullable Object o) {
        if (o == null) {
            return null;
        }
        return (T) o;
    }
}
