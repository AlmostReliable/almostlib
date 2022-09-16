package com.almostreliable.almostlib.util;

import javax.annotation.Nullable;
import java.util.*;

@SuppressWarnings("unused")
public class AlmostUtils {
    public static <T> Optional<T> cast(Object o, Class<T> type) {
        if (type.isInstance(o)) {
            return Optional.of(type.cast(o));
        }

        return Optional.empty();
    }

    @SuppressWarnings("unchecked")
    public static <T> T cast(Object o) {
        return (T) o;
    }

    @Nullable
    @SuppressWarnings("unchecked")
    public static <T> T nullableCast(@Nullable Object o) {
        if (o == null) {
            return null;
        }
        return (T) o;
    }

    public static List<Object> asList(Object o) {
        if (o instanceof List) {
            return cast(o);
        }

        if (o instanceof Object[]) {
            return new ArrayList<>(Arrays.asList((Object[]) o));
        }

        return new ArrayList<>(Collections.singletonList(o));
    }

    public static OptionalInt parseInt(@Nullable Object o) {
        if (o instanceof Number number) {
            return OptionalInt.of(number.intValue());
        }

        if (o instanceof String s) {
            try {
                return OptionalInt.of(Integer.parseInt(s));
            } catch (NumberFormatException ignored) {
            }
        }

        return OptionalInt.empty();
    }

    public static OptionalDouble parseDouble(@Nullable Object o) {
        if (o instanceof Number number) {
            return OptionalDouble.of(number.doubleValue());
        }

        if (o instanceof String s) {
            try {
                return OptionalDouble.of(Double.parseDouble(s));
            } catch (NumberFormatException ignored) {
            }
        }

        return OptionalDouble.empty();
    }
}
