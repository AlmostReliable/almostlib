package com.github.almostreliable.lib.registry;

import com.github.almostreliable.lib.AlmostLib;
import com.github.almostreliable.lib.Utils;
import com.github.almostreliable.lib.registry.builders.*;
import com.mojang.datafixers.util.Function4;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.*;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.material.MaterialColor;

import javax.annotation.Nullable;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;

public abstract class RegistryManager {
    protected final LinkedHashMap<ResourceKey<?>, RegistryDelegate<?>> registries = new LinkedHashMap<>();
    protected final Map<RegistryEntry<? extends BlockEntityType<? extends BlockEntity>>, BlockEntityRendererProvider<? extends BlockEntity>> blockEntityRenderers = new ConcurrentHashMap<>();
    private final String namespace;

    public RegistryManager(String namespace) {
        this.namespace = namespace;
        AlmostLib.LOG.info("RegistryManager created for '{}'", namespace);
    }

    public String getNamespace() {
        return this.namespace;
    }

    public <I extends SwordItem> ItemBuilder<I> itemSword(String id, Tier tier, int atkDamage, int atkSpeed, Function4<Tier, Integer, Integer, Item.Properties, I> factory) {
        return item(id, properties -> factory.apply(tier, atkDamage, atkSpeed, properties));
    }

    public <I extends Item> ItemBuilder<I> item(String id, Function<Item.Properties, I> factory) {
        return new ItemBuilder<I>(id, factory, this, this::finalizeRegistration);
    }

    public <B extends Block, I extends BlockItem> BlockBuilder<B, I> block(String id, Material material, Function<BlockBehaviour.Properties, B> factory) {
        return block(id, BlockBehaviour.Properties.of(material), factory);
    }

    public <B extends Block, I extends BlockItem> BlockBuilder<B, I> block(String id, Material material, MaterialColor color, Function<BlockBehaviour.Properties, B> factory) {
        return block(id, BlockBehaviour.Properties.of(material, color), factory);
    }

    public <B extends Block, I extends BlockItem> BlockBuilder<B, I> block(String id, Material material, DyeColor color, Function<BlockBehaviour.Properties, B> factory) {
        return block(id, BlockBehaviour.Properties.of(material, color), factory);
    }

    public <B extends Block, I extends BlockItem> BlockBuilder<B, I> block(String id, BlockBehaviour.Properties properties, Function<BlockBehaviour.Properties, B> factory) {
        return new BlockBuilder<>(id, properties, factory, this, this::finalizeRegistration);
    }

    public <BE extends BlockEntity> BlockEntityBuilder<BE> blockEntity(String id, BiFunction<BlockPos, BlockState, BE> factory) {
        return new BlockEntityBuilder<>(id, factory, this, this::finalizeRegistration);
    }

    protected abstract <T> RegistryDelegate<T> getOrCreateDelegate(ResourceKey<Registry<T>> resourceKey);

    // I hate generics...
    protected <E, BASE> RegistryEntry<E> finalizeRegistration(RegistryEntryBuilder<E, BASE> builder) {
        RegistryDelegate<E> delegate = Utils.cast(getOrCreateDelegate(builder.getRegistryKey()));
        RegistryEntryData<E> data = RegistryEntryData.of(
                new ResourceLocation(getNamespace(), builder.getName()),
                builder::create
        );
        delegate.add(data);
        if (builder instanceof PostRegisterListener listener) {
            listener.onPostRegister(data.getRegistryEntry());
        }
        return data.getRegistryEntry();
    }

    @Nullable
    public <E> Supplier<E> getEntry(ResourceKey<Registry<E>> resourceKey, String id) {
        RegistryDelegate<?> delegate = registries.get(resourceKey);
        if (delegate == null) {
            return null;
        }
        return Utils.nullableCast(delegate.find(new ResourceLocation(getNamespace(), id)));
    }

    public <E1 extends BASE, E2, BASE> RegistryEntry<E1> getLink(ResourceKey<Registry<BASE>> resourceKey, RegistryEntry<E2> link) {
        RegistryDelegate<?> delegate = registries.get(resourceKey);
        if (delegate == null) {
            throw new IllegalStateException("No registry currently in use for " + resourceKey);
        }

        RegistryEntry<?> registryEntry = delegate.find(link.getRegistryName());
        if (registryEntry == null) {
            throw new IllegalStateException(
                    "No link could be found for " + link.getRegistryName() + " in registry " + delegate.getName());
        }

        return Utils.cast(registryEntry);
    }

//    public <T extends BlockEntity> void registerRenderer(RegistryEntry<? extends BlockEntityType<? extends BlockEntity>> blockEntityType, BlockEntityRendererProvider<T> provider) {
//        blockEntityRenderers.put(blockEntityType, provider);
//    }

    public <T extends BlockEntity> void registerRenderer(RegistryEntry<? extends BlockEntityType<? extends T>> blockEntityType, BlockEntityRendererProvider<T> provider) {
        blockEntityRenderers.put(blockEntityType, provider);
    }

    public void init() {
        List<ResourceKey<?>> priority = List.of(
                Registry.BLOCK_REGISTRY,
                Registry.FLUID_REGISTRY,
                Registry.ITEM_REGISTRY,
                Registry.BLOCK_ENTITY_TYPE_REGISTRY
        );

        List<? extends ResourceKey<? extends Registry<?>>> resourceKeys = Registry.REGISTRY
                .entrySet()
                .stream()
                .map(Map.Entry::getKey)
                .sorted((o1, o2) -> String.valueOf(o1).compareToIgnoreCase(String.valueOf(o2)))
                .sorted(Comparator.comparingInt(value -> {
                    int i = priority.indexOf(value);
                    return i >= 0 ? i : priority.size();
                }))
                .toList();

        for (var resourceKey : resourceKeys) {
            RegistryDelegate<?> registryDelegate = registries.get(resourceKey);
            if (registryDelegate != null) {
                registryDelegate.init();
            }
        }
    }

    public abstract void initClient();
}
