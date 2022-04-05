package com.github.almostreliable.lib.registry;

import com.github.almostreliable.lib.api.registry.IAlmostRegistry;
import com.github.almostreliable.lib.api.registry.RegistryToRuleThemAll;
import com.github.almostreliable.lib.api.registry.builders.ItemBuilder;
import com.github.almostreliable.lib.registry.builders.ItemBuilderImpl;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Item;

import java.util.function.Function;

public abstract class AbstractRegistryToRuleThemAll implements RegistryToRuleThemAll {

    protected final RegistryMap registries = new RegistryMap();
    private final String namespace;

    public AbstractRegistryToRuleThemAll(String namespace) {
        this.namespace = namespace;
    }

    @Override
    public String getNamespace() {
        return this.namespace;
    }

    @Override
    public <T> IAlmostRegistry<T> getOrCreateRegistry(ResourceKey<Registry<T>> resourceKey) {
        return registries.getOrCreate(namespace, resourceKey);
    }

    @Override
    public <I extends Item> ItemBuilder<I> item(String id, Function<Item.Properties, I> factory) {
        return new ItemBuilderImpl<>(this, factory);
    }

    @Override
    public void init() {
        registries.getEntries().forEach(entry -> entry.getValue().init());
    }
}
