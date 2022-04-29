package com.almostreliable.lib.registry;

import com.almostreliable.lib.AlmostLib;
import com.almostreliable.lib.Platform;
import com.almostreliable.lib.Utils;
import com.almostreliable.lib.client.MenuFactory;
import com.almostreliable.lib.client.ScreenFactory;
import com.almostreliable.lib.datagen.DataGenManager;
import com.almostreliable.lib.registry.builders.BlockBuilder;
import com.almostreliable.lib.registry.builders.BlockEntityBuilder;
import com.almostreliable.lib.registry.builders.ItemBuilder;
import com.almostreliable.lib.registry.builders.RegistryEntryBuilder;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import com.mojang.datafixers.util.Function4;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.data.DataGenerator;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.*;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.material.MaterialColor;

import javax.annotation.Nullable;
import java.util.*;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

public abstract class AlmostManager {
    public static final String ALMOST_DATAGEN_PLATFORM = "almost.datagen.platform";
    protected final LinkedHashMap<ResourceKey<?>, RegistryDelegate<?>> registries = new LinkedHashMap<>();
    @Nullable
    protected final List<Consumer<ClientManager>> clientConsumers;
    @Nullable
    protected final Multimap<Enum<Platform>, Consumer<DataGenManager>> datagenConsumers;

    private final String namespace;

    public AlmostManager(String namespace) {
        this.namespace = namespace;
        AlmostLib.LOG.info("AlmostManager created for '{}'", namespace);
        clientConsumers = AlmostLib.INSTANCE.isClient() ? new ArrayList<>() : null;
        datagenConsumers = AlmostLib.INSTANCE.isDataGenEnabled() ? ArrayListMultimap.create() : null;
    }

    public String getNamespace() {
        return this.namespace;
    }

    public CreativeModeTab tab(String translation, Supplier<ItemStack> iconSupplier) {
        String translationKey = translation.toLowerCase(Locale.ENGLISH).replaceAll("\\s+", "");
        addOnDataGen(manager -> {
            manager.getLangProvider().addLang("itemGroup." + namespace + "." + translationKey, translation);
        });
        return AlmostLib.INSTANCE.createCreativeTab(new ResourceLocation(namespace, translationKey), iconSupplier);
    }

    public <I extends SwordItem> ItemBuilder<I> itemSword(String id, Tier tier, int atkDamage, int atkSpeed, Function4<Tier, Integer, Integer, Item.Properties, I> factory) {
        return item(id, properties -> factory.apply(tier, atkDamage, atkSpeed, properties));
    }

    public <I extends DiggerItem> ItemBuilder<I> itemTool(String id, Tier tier, int atkDamage, int atkSpeed, Function4<Tier, Integer, Integer, Item.Properties, I> factory) {
        return item(id, properties -> factory.apply(tier, atkDamage, atkSpeed, properties));
    }

    public <I extends Item> ItemBuilder<I> item(String id, Function<Item.Properties, I> factory) {
        return new ItemBuilder<I>(id, factory, this, this::onRegisterEntry);
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
        return new BlockBuilder<>(id, properties, factory, this, this::onRegisterEntry);
    }

    public <BE extends BlockEntity> BlockEntityBuilder<BE> blockEntity(String id, BiFunction<BlockPos, BlockState, BE> factory) {
        return new BlockEntityBuilder<>(id, factory, this, this::onRegisterEntry);
    }

    public <M extends AbstractContainerMenu, S extends AbstractContainerScreen<M>, BE extends BlockEntity> RegistryEntry<MenuType<M>> menuBlockEntity(String id,
                                                                                                                                                      MenuFactory.ForBlockEntityAndInventory<M, BE> menuFactory,
                                                                                                                                                      Supplier<ScreenFactory<M, S>> screenFactory) {
        return menu(id, (windowId, inventory, buffer) -> {
            BE entity = Utils.nullableCast(inventory.player.level.getBlockEntity(buffer.readBlockPos()));
            if (entity == null) {
                throw new IllegalStateException("Wrong BlockEntity usage.");
            }
            return menuFactory.apply(windowId, inventory, entity);
        }, screenFactory);
    }

    public TagKey<Item> createItemTag(String tag) {
        return createTag(Registry.ITEM_REGISTRY, tag);
    }

    public TagKey<Block> createBlockTag(String tag) {
        return createTag(Registry.BLOCK_REGISTRY, tag);
    }

    public TagKey<EntityType<?>> createEntityTag(String tag) {
        return createTag(Registry.ENTITY_TYPE_REGISTRY, tag);
    }

    public <T> TagKey<T> createTag(ResourceKey<? extends Registry<T>> resourceKey, String tag) {
        return TagKey.create(resourceKey, new ResourceLocation(namespace, tag));
    }

    public <M extends AbstractContainerMenu, S extends AbstractContainerScreen<M>, BE extends BlockEntity> RegistryEntry<MenuType<M>> menuBlockEntity(String id,
                                                                                                                                                      MenuFactory.ForBlockEntity<M, BE> menuFactory,
                                                                                                                                                      Supplier<ScreenFactory<M, S>> screenFactory) {
        return menu(id, (windowId, inventory, buffer) -> {
            BE entity = Utils.nullableCast(inventory.player.level.getBlockEntity(buffer.readBlockPos()));
            if (entity == null) {
                throw new IllegalStateException("Wrong BlockEntity usage.");
            }
            return menuFactory.apply(windowId, entity);
        }, screenFactory);
    }


    public <M extends AbstractContainerMenu, S extends AbstractContainerScreen<M>> RegistryEntry<MenuType<M>> menu(String id,
                                                                                                                   MenuFactory<M> menuFactory,
                                                                                                                   Supplier<ScreenFactory<M, S>> screenFactory) {
        RegistryEntryData<MenuType<M>> data = createRegistryEntryData(Registry.MENU_REGISTRY,
                id,
                () -> AlmostLib.INSTANCE.createMenuType(menuFactory));
        addOnClientInit(cm -> cm.registerScreen(data.getRegistryEntry().get(), screenFactory.get()));
        return data.getRegistryEntry();
    }


    protected abstract <T> RegistryDelegate<T> getOrCreateDelegate(ResourceKey<Registry<T>> resourceKey);

    protected <E, BASE> RegistryEntry<E> onRegisterEntry(RegistryEntryBuilder<E, BASE> builder) {
        RegistryEntryData<E> data = createRegistryEntryData(builder.getRegistryKey(),
                builder.getName(),
                builder::create);
        builder.onRegister(data.getRegistryEntry());
        return data.getRegistryEntry();
    }

    protected <E, BASE> RegistryEntryData<E> createRegistryEntryData(ResourceKey<Registry<BASE>> resourceKey, String name, Supplier<E> supplier) {
        RegistryDelegate<E> delegate = Utils.cast(getOrCreateDelegate(resourceKey));
        RegistryEntryData<E> data = RegistryEntryData.of(
                new ResourceLocation(getNamespace(), name),
                supplier
        );
        delegate.add(data);
        return data;
    }

    @Nullable
    public <E> Supplier<E> getEntry(ResourceKey<Registry<E>> resourceKey, String id) {
        RegistryDelegate<?> delegate = registries.get(resourceKey);
        if (delegate == null) {
            return null;
        }
        return Utils.nullableCast(delegate.find(new ResourceLocation(getNamespace(), id)));
    }

    /**
     * Get the linked entry from a different registry. Can be used to find the corresponding {@link BlockItem} for a {@link Block}.
     * Matching links is based on entry id.
     *
     * @param resourceKey the registry to lookup
     * @param link        the linked entry to use for the lookup
     * @return the entry which matches the link
     */
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

    /**
     * Register consumer to be called when client is initialized.
     *
     * @param consumer consumer to be called
     */
    public void addOnClientInit(Consumer<ClientManager> consumer) {
        if (clientConsumers != null) {
            clientConsumers.add(consumer);
        }
    }

    /**
     * Register consumer to be called when data gen happens
     *
     * @param platform the platform to register for
     * @param consumer consumer to be called
     */
    public void addOnDataGen(Platform platform, Consumer<DataGenManager> consumer) {
        if (datagenConsumers != null) {
            datagenConsumers.put(platform, consumer);
        }
    }

    /**
     * Register consumer to be called when data gen happens
     *
     * @param consumer consumer to be called
     */
    public void addOnDataGen(Consumer<DataGenManager> consumer) {
        if (datagenConsumers != null) {
            datagenConsumers.put(Platform.COMMON, consumer);
        }
    }

    /**
     * Fabric: Call this in your "onInitialize" by implementing ModInitializer.
     * <p>
     * Forge: Call this in your mod constructor. The manager will register the events automatically.
     */
    public void registerManager() {
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

    /**
     * Fabric: Call this in your "onInitializeClient" by implementing ClientModInitializer.
     * <p>
     * Forge: Call this in your mod constructor. The manager will register the events automatically.
     */
    public abstract void registerClientManager();

    /**
     * Fabric: Call this in your "onInitializeDataGenerator" by implementing DataGeneratorEntrypoint.
     * <p>
     * Forge: Call this in your "GatherDataEvent" event.
     *
     * @param dataGenerator The data generator to use
     */
    public void registerDataGen(DataGenerator dataGenerator) {
        Objects.requireNonNull(datagenConsumers, "registerDataGen was called outside of the data gen environment");
        DataGenManager dataGenManager = new DataGenManager(getNamespace(), dataGenerator);

        String property = System.getProperty(ALMOST_DATAGEN_PLATFORM);
        Platform platform = switch (property != null ? property : "common") {
            case "common" -> Platform.COMMON;
            case "fabric" -> Platform.FABRIC;
            case "forge" -> Platform.FORGE;
            default -> throw new IllegalStateException("Unknown platform: " + property);
        };

        switch (platform) {
            case FABRIC -> {
                datagenConsumers.get(Platform.FABRIC).forEach(consumer -> consumer.accept(dataGenManager));
                datagenConsumers.get(Platform.LOADER).forEach(consumer -> consumer.accept(dataGenManager));
            }
            case FORGE -> {
                datagenConsumers.get(Platform.FORGE).forEach(consumer -> consumer.accept(dataGenManager));
                datagenConsumers.get(Platform.LOADER).forEach(consumer -> consumer.accept(dataGenManager));
            }
            case COMMON -> datagenConsumers.get(Platform.COMMON).forEach(consumer -> consumer.accept(dataGenManager));
        }

        dataGenManager.collectDataProvider(dataGenerator);
    }

}
