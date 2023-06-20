package com.almostreliable.almostlib.registry;

import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Supplier;

public abstract class Registration<S, RE extends RegistryEntry<? extends S>> {

    protected final Map<ResourceLocation, RE> entries = new LinkedHashMap<>();
    private final String namespace;
    private final Registry<S> registry;
    private final Collection<RE> entriesView = Collections.unmodifiableCollection(entries.values());

    public Registration(String namespace, Registry<S> registry) {
        this.namespace = namespace;
        this.registry = registry;
    }

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

    public static MenuRegistration menus(String namespace) {
        return new MenuRegistration(namespace);
    }

    public static RecipeRegistration recipes(String namespace) {
        return new RecipeRegistration(namespace);
    }

    public void applyRegister(BiConsumer<ResourceLocation, S> callback) {
        getEntries().forEach(entry -> callback.accept(entry.getId(), entry.get()));
    }

    protected abstract RE createEntry(ResourceLocation id, Supplier<? extends S> supplier);

    RE createOrThrowEntry(String id, Supplier<? extends S> supplier) {
        final ResourceLocation fullId = new ResourceLocation(namespace, id);
        if (entries.containsKey(fullId)) {
            throw new IllegalArgumentException("Duplicate registration for " + id + " in " + namespace);
        }
        RE entry = createEntry(fullId, supplier);
        entries.put(fullId, entry);
        return entry;
    }

    public Collection<RE> getEntries() {
        return entriesView;
    }

    public String getNamespace() {
        return namespace;
    }

    public Registry<S> getRegistry() {
        return registry;
    }
}
