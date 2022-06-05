package com.almostreliable.lib;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nullable;

public class Utils {
    public static final Logger LOG = LoggerFactory.getLogger(BuildConfig.MOD_NAME);

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
