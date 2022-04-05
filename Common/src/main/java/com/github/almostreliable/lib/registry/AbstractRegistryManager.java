package com.github.almostreliable.lib.registry;

import com.github.almostreliable.lib.api.AlmostLib;
import com.github.almostreliable.lib.api.registry.RegistryDelegate;
import com.github.almostreliable.lib.api.registry.RegistryManager;
import com.github.almostreliable.lib.api.registry.builders.ItemBuilder;
import com.github.almostreliable.lib.registry.builders.ItemBuilderImpl;
import net.minecraft.core.NonNullList;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;

import java.util.function.Function;
import java.util.function.Supplier;

public abstract class AbstractRegistryManager implements RegistryManager {
    protected final NonNullList<RegistryDelegate<?>> registries = NonNullList.create();
    private final String namespace;
    protected final Supplier<String> namespaceSupplier = this::getNamespace;
    protected final RegistryDelegate<Block> blocks = createRegistry(Registry.BLOCK_REGISTRY);
    protected final RegistryDelegate<Item> items = createRegistry(Registry.ITEM_REGISTRY);

    public AbstractRegistryManager(String namespace) {
        this.namespace = namespace;
    }

    @Override
    public String getNamespace() {
        return this.namespace;
    }

    @Override
    public <I extends Item> ItemBuilder<I> item(String id, Function<Item.Properties, I> factory) {
        return new ItemBuilderImpl<>(this, factory);
    }

    private <T> RegistryDelegate<T> createRegistry(ResourceKey<Registry<T>> resourceKey) {
        RegistryDelegate<T> delegate = AlmostLib.INSTANCE.createRegistryDelegate(namespaceSupplier,
                resourceKey);
        registries.add(delegate);
        return delegate;
    }
}
