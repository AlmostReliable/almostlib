package com.github.almostreliable.lib.registry;

import com.github.almostreliable.lib.Utils;
import net.minecraft.resources.ResourceLocation;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public abstract class RegistryDelegate<T> {
    protected final Map<ResourceLocation, RegistryEntryData<T>> entries = new HashMap<>();

    public void add(RegistryEntryData<T> registryEntryData) {
        Objects.requireNonNull(registryEntryData);

        if (entries.containsKey(registryEntryData.getRegistryName())) {
            throw new IllegalArgumentException("Duplicate entry registration: " + registryEntryData.getRegistryName());
        }

        entries.put(registryEntryData.getRegistryName(), registryEntryData);
    }

    public boolean isPresent() {
        return !entries.isEmpty();
    }

    @Nullable
    public <E extends T> RegistryEntry<E> find(ResourceLocation registryName) {
        RegistryEntryData<T> data = entries.get(registryName);
        if (data == null) {
            return null;
        }

        return Utils.cast(data.getRegistryEntry());
    }

    public abstract void init();

    public abstract String getName();
}
