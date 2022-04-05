package com.github.almostreliable.lib.registry;

import com.github.almostreliable.lib.api.AlmostLib;
import com.github.almostreliable.lib.api.registry.RegistryDelegate;
import com.github.almostreliable.lib.api.registry.RegistryManager;
import com.github.almostreliable.lib.api.registry.builders.BlockBuilder;
import com.github.almostreliable.lib.api.registry.builders.ItemBuilder;
import com.github.almostreliable.lib.registry.builders.BlockBuilderImpl;
import com.github.almostreliable.lib.registry.builders.ItemBuilderImpl;
import com.mojang.datafixers.util.Function4;
import net.minecraft.core.NonNullList;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.*;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.material.MaterialColor;

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
    public <I extends SwordItem> ItemBuilder<I> itemSword(String id, Tier tier, int atkDamage, int atkSpeed, Function4<Tier, Integer, Integer, Item.Properties, I> factory) {
        return item(id, properties -> factory.apply(tier, atkDamage, atkSpeed, properties));
    }

    @Override
    public <I extends Item> ItemBuilder<I> item(String id, Function<Item.Properties, I> factory) {
        return new ItemBuilderImpl<I>(id, factory, (id1, entrySupplier) -> {
            // TODO Datagen and all the stuff
            return items.register(id1, entrySupplier);
        });
    }

    @Override
    public <B extends Block, I extends BlockItem> BlockBuilder<B, I> block(String id, Material material, Function<BlockBehaviour.Properties, B> factory) {
        return block(id, BlockBehaviour.Properties.of(material), factory);
    }

    @Override
    public <B extends Block, I extends BlockItem> BlockBuilder<B, I> block(String id, Material material, MaterialColor color, Function<BlockBehaviour.Properties, B> factory) {
        return block(id, BlockBehaviour.Properties.of(material, color), factory);
    }

    @Override
    public <B extends Block, I extends BlockItem> BlockBuilder<B, I> block(String id, Material material, DyeColor color, Function<BlockBehaviour.Properties, B> factory) {
        return block(id, BlockBehaviour.Properties.of(material, color), factory);
    }

    @Override
    public <B extends Block, I extends BlockItem> BlockBuilder<B, I> block(String id, BlockBehaviour.Properties properties, Function<BlockBehaviour.Properties, B> factory) {
        return new BlockBuilderImpl<>(id, properties, factory, (id1, entrySupplier) -> {
            return blocks.register(id1, entrySupplier);
        }, (id1, entrySupplier) -> {return null;});
    }

    private <T> RegistryDelegate<T> createRegistry(ResourceKey<Registry<T>> resourceKey) {
        RegistryDelegate<T> delegate = AlmostLib.INSTANCE.createRegistryDelegate(namespaceSupplier,
                resourceKey);
        registries.add(delegate);
        return delegate;
    }

    @Override
    public void init() {
        // TODO: Think about ordering for fabric
        for (RegistryDelegate<?> registry : registries) {
            if (registry.isPresent()) {
                registry.init();
            }
        }
    }
}
