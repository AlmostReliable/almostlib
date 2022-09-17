package com.almostreliable.almostlib.registry;

import com.google.common.base.Suppliers;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Supplier;

public abstract class Registration<S, RE extends RegistryEntry<? extends S>> {

    public static ItemRegistration items(String namespace) {
        return new ItemRegistration(namespace, Registry.ITEM);
    }

    public static BlockRegistration blocks(String namespace) {
        return new BlockRegistration(namespace, Registry.BLOCK);
    }

    public static <T> GenericRegistration<T> generic(String namespace, Registry<T> registry) {
        return new GenericRegistration<>(namespace, registry);
    }

    public static BlockEntityRegistration blockEntities(String namespace) {
        return new BlockEntityRegistration(namespace, Registry.BLOCK_ENTITY_TYPE);
    }

    private final String namespace;
    private final Registry<S> registry;

    private final Map<ResourceLocation, RegistryEntry<? extends S>> entries = new HashMap<>();
    private final Collection<RegistryEntry<? extends S>> entriesView = Collections.unmodifiableCollection(entries.values());

    public Registration(String namespace, Registry<S> registry) {
        this.namespace = namespace;
        this.registry = registry;
    }

    public Collection<RegistryEntry<? extends S>> getEntries() {
        return entriesView;
    }

    public String getNamespace() {
        return namespace;
    }

    public Registry<S> getRegistry() {
        return registry;
    }

    protected abstract RE createEntry(ResourceLocation id, Supplier<? extends S> supplier);

    @SuppressWarnings("unchecked")
    public <T extends S> RegistryEntry<T> register(String name, Supplier<? extends T> supplier) {
        final ResourceLocation id = new ResourceLocation(namespace, name);
        if (entries.containsKey(id)) {
            throw new IllegalArgumentException("Duplicate registration for " + name + " in " + namespace);
        }
        Supplier<T> lazy = Suppliers.memoize(supplier::get);
        RE e = createEntry(id, lazy);
        entries.put(id, e);
        return (RegistryEntry<T>) e;
    }

    public void applyRegister(BiConsumer<ResourceLocation, S> callback) {
        getEntries().forEach(entry -> callback.accept(entry.getId(), entry.get()));
    }
}
