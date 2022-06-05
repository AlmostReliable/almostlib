package com.almostreliable.lib.config;

import com.almostreliable.lib.Utils;
import com.almostreliable.lib.utils.BooleanRef;
import com.google.common.base.Preconditions;
import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;

import javax.annotation.Nullable;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.Map;
import java.util.Set;

public class ConfigTypeAdapterFactory implements TypeAdapterFactory {

    private final String configName;
    private final Set<Class<?>> classes;
    private final BooleanRef errorHappened;

    public ConfigTypeAdapterFactory(String configName, Set<Class<?>> classes, BooleanRef errorHappened) {
        for (Class<?> c : classes) {
            Preconditions.checkArgument(ConfigHelper.isUsableTypeForConfig(c),
                    "Type adapter factory can only be used for classes that are represented as JSON objects");
        }
        this.configName = configName;
        this.classes = classes;
        this.errorHappened = errorHappened;
    }

    @Override
    public <T> TypeAdapter<T> create(Gson gson, TypeToken<T> type) {
        if (classes.contains(type.getRawType())) {
            //noinspection unchecked
            return new ConfigTypeAdapter<>(configName, (Class<T>) type.getRawType(), errorHappened, gson);
        }

        return null;
    }

    public static class ConfigTypeAdapter<T> extends TypeAdapter<T> {
        protected final String configName;
        private final Class<T> clazz;
        private final Gson gson;
        private final BooleanRef errorHappened;

        public ConfigTypeAdapter(String configName, Class<T> clazz, BooleanRef errorHappened, Gson gson) {
            this.configName = configName;
            this.clazz = clazz;
            this.errorHappened = errorHappened;
            this.gson = gson;
        }

        @Override
        public void write(JsonWriter out, T value) throws IOException {
            Map<String, Field> fields = ConfigHelper.getFields(clazz);

            out.beginObject();
            for (var entry : fields.entrySet()) {
                Field field = entry.getValue();
                String name = entry.getKey();
                try {
                    field.setAccessible(true);
                    Object fieldValue = field.get(value);
                    if (fieldValue != null) {
                        //noinspection rawtypes
                        TypeAdapter adapter = gson.getAdapter(field.getType());
                        out.name(name);
                        //noinspection unchecked
                        adapter.write(out, fieldValue);
                    }
                } catch (Exception e) {
                    Utils.LOG.error("[Config {}] Failed to write field {}", configName, field.getName(), e);
                }
            }
            out.endObject();
        }

        @Override
        public T read(JsonReader in) throws IOException {
            T instance = ConfigHelper.createInstance(clazz);
            Map<String, Field> fields = ConfigHelper.getFields(clazz);

            in.beginObject();
            while (in.hasNext()) {
                String name = in.nextName();
                Field field = fields.remove(name);
                if (field == null) {
                    continue;
                }
                readField(in, instance, name, field);
            }
            in.endObject();

            if (!fields.isEmpty()) {
                Utils.LOG.warn("]Config {}] Missing fields ({}) found. Adding them.",
                        configName,
                        fields.keySet());
                errorHappened.set(true);
            }

            return instance;
        }

        protected void readField(JsonReader in, T instance, String name, Field field) {
            try {
                JsonToken token = in.peek();
                if (tokenMatchesType(token, field.getType())) {
                    TypeAdapter<?> safeAdapter = getSafeAdapter(field.getType());
                    TypeAdapter<?> adapter = gson.getAdapter(field.getType());

                    field.setAccessible(true);
                    if (safeAdapter == null) {
                        Object value = adapter.read(in);
                        field.set(instance, value);
                    } else {
                        Object safeValue = safeAdapter.read(in);
                        Object value = adapter.fromJsonTree((JsonElement) safeValue);
                        field.set(instance, value);
                    }
                } else {
                    in.skipValue();
                    Utils.LOG.warn(
                            "[Config {}] Type mismatch for field {}, required type {}",
                            configName, field.getName(), field.getType());
                    errorHappened.set(true);
                }

            } catch (Exception e) {
                Utils.LOG.warn("[Config {}] Failed to read field {}, because of: {}", configName, name, e.getMessage());
                errorHappened.set(true);
            }
        }

        /**
         * Returns a safe adapter that can be used to read the field.
         * If we use them, it makes sure our {@link JsonReader} will consume the tokens.
         * <p>
         * If reading throws an exception for our safe adapter, the tokens are consumed anyway, and we can just skip the field.
         *
         * @param clazz The type of the field.
         * @return A safe adapter or null if no safe adapter is available.
         */
        @Nullable
        protected TypeAdapter<? extends JsonElement> getSafeAdapter(Class<?> clazz) {
            if (ConfigHelper.isMap(clazz)) {
                return gson.getAdapter(JsonObject.class);
            }

            if (ConfigHelper.isCollectionOrArray(clazz)) {
                return gson.getAdapter(JsonArray.class);
            }

            if (ConfigHelper.isPrimitiveOrString(clazz)) {
                return gson.getAdapter(JsonPrimitive.class);
            }

            return null;
        }

        /**
         * Checks if a token will match our type. If the token does not match, the adapter will skip reading.
         * the field.
         *
         * @param token The token to check
         * @param clazz The class to check against
         * @return true if matches, false otherwise
         */
        protected boolean tokenMatchesType(JsonToken token, Class<?> clazz) {
            return switch (token) {
                case BEGIN_ARRAY -> ConfigHelper.isCollectionOrArray(clazz);
                case BEGIN_OBJECT -> ConfigHelper.isMap(clazz) || ConfigHelper.isUsableTypeForConfig(clazz);
                case BOOLEAN, NUMBER -> ConfigHelper.isPrimitiveOrString(clazz);
                case STRING -> ConfigHelper.isPrimitiveOrString(clazz) || clazz.isEnum();
                default -> true; // TODO decide if NULL should be true too
            };
        }
    }
}
