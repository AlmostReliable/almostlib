package com.github.almostreliable.lib.registry;

import com.github.almostreliable.lib.api.Utils;
import com.github.almostreliable.lib.api.registry.RegistryEntry;
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

//    protected static class Entry<T> {
//        protected RegistryEntry<T> registryEntry;
//        @Nullable
//        private Supplier<T> supplier;
//
//        Entry(Supplier<T> supplier, RegistryEntry<T> registryEntry) {
//            this.supplier = supplier;
//            this.registryEntry = registryEntry;
//        }
//
//        void updateReference(ResourceLocation id) {
//            if (supplier == null) {
//                throw new IllegalStateException("Reference cannot be updated twice");
//            }
//
//            T value = supplier.get();
//            registryEntry.updateReference(id, value);
//            supplier = null;
//        }
//    }
}
