package com.almostreliable.lib.config;

import com.almostreliable.lib.AlmostLib;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.function.Function;

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

    public static <T> List<T> toList(Object o, Class<T> type, Function<Object, T> converter) {
        if (!(o instanceof List<?> list)) {
            throw new RuntimeException("Could not convert " + o + " to list");
        }

        List<T> result = new ArrayList<>();
        for (Object element : list) {
            try {
                result.add(converter.apply(element));
            } catch (Exception e) {
                AlmostLib.LOGGER.warn("Could not convert " + element + " to " + type + ". Will be skipped.");
            }
        }

        return result;
    }
}
