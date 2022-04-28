package com.almostreliable.lib.registry.builders;

import com.almostreliable.lib.datagen.TagsProvider;
import com.almostreliable.lib.registry.RegistryEntry;
import com.almostreliable.lib.datagen.ItemModelProvider;
import com.almostreliable.lib.registry.AlmostManager;
import com.almostreliable.lib.registry.RegisterCallback;
import com.google.gson.JsonObject;
import net.minecraft.core.Registry;
import net.minecraft.data.models.model.ModelTemplate;
import net.minecraft.data.models.model.ModelTemplates;
import net.minecraft.data.models.model.TextureMapping;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;

import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Supplier;

public class ItemBuilder<I extends Item> extends AbstractEntryBuilder<I, Item, ItemBuilder<I>> {
    private final Function<Item.Properties, ? extends I> factory;
    private final List<BiConsumer<RegistryEntry<I>, ItemModelProvider>> itemModelGenerators;
    private final Set<TagKey<Item>> itemTags = new HashSet<>();
    private Item.Properties properties;

    public ItemBuilder(String id, Function<Item.Properties, ? extends I> factory, AlmostManager manager, RegisterCallback registerCallback) {
        super(id, manager, registerCallback);
        this.factory = factory;
        this.properties = new Item.Properties();
        this.itemModelGenerators = new ArrayList<>();
    }

    public ItemBuilder<I> properties(Supplier<Item.Properties> supplier) {
        properties = supplier.get();

        Objects.requireNonNull(properties);
        return this;
    }

    public ItemBuilder<I> food(FoodProperties food) {
        properties.food(food);
        return this;
    }

    public ItemBuilder<I> maxStackSize(int size) {
        properties.stacksTo(size);
        return this;
    }

    public ItemBuilder<I> defaultDurability(int durability) {
        properties.defaultDurability(durability);
        return this;
    }

    public ItemBuilder<I> durability(int durability) {
        properties.durability(durability);
        return this;
    }

    public ItemBuilder<I> craftRemainder(ItemLike itemLike) {
        properties.craftRemainder(itemLike.asItem());
        return this;
    }

    public ItemBuilder<I> tab(CreativeModeTab tab) {
        properties.tab(tab);
        return this;
    }

    public ItemBuilder<I> rarity(Rarity rarity) {
        properties.rarity(rarity);
        return this;
    }

    public ItemBuilder<I> fireResistant() {
        properties.fireResistant();
        return this;
    }

    public ItemBuilder<I> defaultLang(String value) {
        return lang(Item::getDescriptionId, $ -> value);
    }

    public ItemBuilder<I> defaultLang() {
        return defaultLang(nameToLang());
    }

    public ItemBuilder<I> lang(ItemLike itemLike, String value) {
        return lang(i -> {
            Item item = itemLike.asItem();
            return i.getDescriptionId(new ItemStack(item));
        }, i -> value);
    }

    public ItemBuilder<I> defaultModel() {
        return model(ModelTemplates.FLAT_ITEM);
    }

    public ItemBuilder<I> defaultHandheldModel() {
        return model(ModelTemplates.FLAT_HANDHELD_ITEM);
    }

    public ItemBuilder<I> defaultBlockItemModel(RegistryEntry<? extends Block> blockRegistryEntry) {
        return model((registryEntry, itemModelProvider) -> {
            ResourceLocation blockLocation = new ResourceLocation(registryEntry.getRegistryName().getNamespace(),
                    "block/" + registryEntry.getRegistryName().getPath());

            itemModelProvider.addModel(registryEntry.getRegistryName(), () -> {
                JsonObject json = new JsonObject();
                json.addProperty("parent", blockLocation.toString());
                return json;
            });
        });
    }

    public ItemBuilder<I> model(ModelTemplate template) {
        itemModelGenerators.add((registryEntry, itemModelProvider) -> {
            template.create(registryEntry.getRegistryName(),
                    TextureMapping.layer0(registryEntry.get()),
                    itemModelProvider.getModelConsumer());
        });
        return this;
    }

    public ItemBuilder<I> model(BiConsumer<RegistryEntry<I>, ItemModelProvider> model) {
        itemModelGenerators.add(model);
        return this;
    }

    @SafeVarargs
    public final ItemBuilder<I> tags(TagKey<Item>... tags) {
        itemTags.addAll(Arrays.asList(tags));
        return this;
    }

    @Override
    public I create() {
        return factory.apply(properties);
    }

    @Override
    public ResourceKey<Registry<Item>> getRegistryKey() {
        return Registry.ITEM_REGISTRY;
    }

    @Override
    public void onRegister(RegistryEntry<I> registryEntry) {
        super.onRegister(registryEntry);
        manager.addOnDataGen(dataGenManager -> {
            itemModelGenerators.forEach(consumer -> {
                consumer.accept(registryEntry, dataGenManager.getItemModelProvider());
            });

            TagsProvider<Item> tagsProvider = dataGenManager.getTagsProvider(Registry.ITEM);
            for (TagKey<Item> tag : itemTags) {
                tagsProvider.tag(tag).add(registryEntry.get());
            }
        });
    }
}
