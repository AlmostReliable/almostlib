package com.almostreliable.lib.config;

import org.apache.commons.lang3.ClassUtils;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class ConfigHelper {
    protected static boolean isPrimitiveOrString(Class<?> clazz) {
        return ClassUtils.isPrimitiveOrWrapper(clazz) || clazz == String.class;
    }

    protected static boolean isCollectionOrArray(Class<?> clazz) {
        return Collection.class.isAssignableFrom(clazz) || clazz.isArray();
    }

    protected static boolean isMap(Class<?> clazz) {
        return Map.class.isAssignableFrom(clazz);
    }

    protected static boolean isUsableTypeForConfig(Class<?> clazz) {
        return !(isPrimitiveOrString(clazz) || isCollectionOrArray(clazz) ||
                 Map.class.isAssignableFrom(clazz) || clazz.isEnum());
    }

    protected static Map<String, Field> getFields(Object obj) {
        return getFields(obj.getClass());
    }

    protected static Map<String, Field> getFields(Class<?> clazz) {
        Map<String, Field> fields = new HashMap<>();

        Class<?> currentClass = clazz;
        while (currentClass != null && currentClass != Object.class) {
            for (Field f : currentClass.getDeclaredFields()) {
                if (!fields.containsKey(f.getName())) {
                    fields.put(f.getName(), f);
                }
            }
            currentClass = currentClass.getSuperclass();
        }

        fields
                .entrySet()
                .removeIf(e -> e.getValue().getAnnotation(ConfigIgnore.class) != null ||
                               Modifier.isStatic(e.getValue().getModifiers()));
        return fields;
    }

    public static <T> T createInstance(Class<T> clazz) {
        try {
            return clazz.getConstructor(new Class[]{}).newInstance();
        } catch (Exception e) {
            throw new IllegalStateException("Failed to create instance of class " + clazz.getName(), e);
        }
    }
}
