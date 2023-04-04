package com.almostreliable.almostlib.config;

import java.util.Locale;

public class Conversions {

    public static int toInt(Object o) {
        return ((Number) o).intValue();
    }

    public static long toLong(Object o) {
        return ((Number) o).longValue();
    }

    public static float toFloat(Object o) {
        return ((Number) o).floatValue();
    }

    public static double toDouble(Object o) {
        return ((Number) o).doubleValue();
    }

    public static boolean toBoolean(Object o) {
        return (boolean) o;
    }

    public static String toString(Object o) {
        return (String) o;
    }

    public static <T extends Enum<T>> T toEnum(Object o, Class<T> enumType) {
        if (o instanceof String str) {
            return Enum.valueOf(enumType, str.toUpperCase(Locale.ROOT));
        }

        throw new RuntimeException("Could not convert " + o + " to enum " + enumType.getSimpleName());
    }
}
