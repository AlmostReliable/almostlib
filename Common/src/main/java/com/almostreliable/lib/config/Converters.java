package com.almostreliable.lib.config;

import com.almostreliable.lib.AlmostLib;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.function.Function;

/**
 * Utility class for converting raw values to their actual types.
 * <p>
 * Used internally by the config library.
 */
public final class Converters {

    private Converters() {}

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

    public static <T extends Enum<T>> T toEnum(Object o, Class<T> clazz) {
        if (!(o instanceof String str)) {
            throw new IllegalArgumentException(o + " is not a string");
        }

        try {
            return Enum.valueOf(clazz, str.toUpperCase(Locale.ROOT));
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Could not convert " + o + " to enum " + clazz.getSimpleName());
        }
    }

    public static <T> List<T> toList(Object o, Class<T> elementClazz, Function<Object, T> elementConverter) {
        if (!(o instanceof List<?> list)) {
            throw new IllegalArgumentException(o + " is not a list");
        }

        List<T> result = new ArrayList<>();
        for (Object element : list) {
            try {
                result.add(elementConverter.apply(element));
            } catch (IllegalArgumentException | ClassCastException e) {
                AlmostLib.LOGGER.warn("Could not convert " + element + " to " + elementClazz.getSimpleName() + ", skipping");
            }
        }

        return result;
    }
}
