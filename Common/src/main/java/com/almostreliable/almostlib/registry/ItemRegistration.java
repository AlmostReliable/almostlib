package com.almostreliable.almostlib.registry;

import com.almostreliable.almostlib.datagen.DataGenHolder;
import com.almostreliable.almostlib.datagen.DataGenManager;
import com.almostreliable.almostlib.datagen.provider.ItemModelProvider;
import com.almostreliable.almostlib.mixin.ItemPropertiesAccessor;
import com.almostreliable.almostlib.util.AlmostUtils;
import net.minecraft.core.Registry;
import net.minecraft.data.models.model.ModelLocationUtils;
import net.minecraft.data.models.model.ModelTemplate;
import net.minecraft.data.models.model.ModelTemplates;
import net.minecraft.data.models.model.TextureMapping;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.level.ItemLike;

import javax.annotation.Nullable;
import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Supplier;

public class ItemRegistration extends Registration<Item, ItemEntry<? extends Item>> implements DataGenHolder {

    @Nullable private CreativeModeTab defaultCreativeTab;
    private DataGenManager dataGenManager;

    ItemRegistration(String namespace, Registry<Item> registry) {
        super(namespace, registry);
    }

    @Override
    protected ItemEntry<? extends Item> createEntry(ResourceLocation id, Supplier<? extends Item> supplier) {
        return new ItemEntry<>(getRegistry(), id, AlmostUtils.cast(supplier));
    }

    @Nullable
    public CreativeModeTab getDefaultCreativeTab() {
        return defaultCreativeTab;
    }

    public ItemRegistration defaultCreativeTab(CreativeModeTab tab) {
        this.defaultCreativeTab = tab;
        return this;
    }

    public <T extends Item> ItemEntry<T> register(String id, Supplier<? extends T> supplier) {
        return AlmostUtils.cast(createOrThrowEntry(id, supplier));
    }

    public <T extends Item> ItemEntry<T> simple(String id, Function<Item.Properties, ? extends T> factory) {
        return AlmostUtils.cast(builder(id, factory).simpleModel().register());
    }

    public <T extends Item> ItemEntry<T> simple(String id, String englishName, Function<Item.Properties, ? extends T> factory) {
        return AlmostUtils.cast(builder(id, factory).defaultLang(englishName).simpleModel().register());
    }

    public <I extends Item> Builder<I> builder(String id, Function<Item.Properties, ? extends I> factory) {
        return new Builder<>(id, factory);
    }

    public Builder<Item> builder(String id) {
        return new Builder<>(id, Item::new);
    }

    public ItemRegistration dataGen(DataGenManager dataGenManager) {
        this.dataGenManager = dataGenManager;
        return this;
    }

    @Nullable
    @Override
    public DataGenManager getDataGenManager() {
        return this.dataGenManager;
    }

    public class Builder<I extends Item> {

        private final Function<Item.Properties, ? extends I> factory;
        private final String id;
        private Item.Properties properties;
        private final List<BiConsumer<RegistryEntry<I>, ItemModelProvider>> itemModelGenerators = new ArrayList<>();
        private final Set<TagKey<Item>> tags = new HashSet<>();

        @Nullable private String defaultLang;
        private boolean noLang;

        public Builder(String id, Function<Item.Properties, ? extends I> factory) {
            this.id = id;
            this.factory = factory;
            this.properties = new Item.Properties();
        }

        public Builder<I> properties(Supplier<Item.Properties> supplier) {
            properties = supplier.get();
            Objects.requireNonNull(properties);
            return this;
        }

        public Builder<I> food(FoodProperties food) {
            properties.food(food);
            return this;
        }

        public Builder<I> maxStackSize(int size) {
            properties.stacksTo(size);
            return this;
        }

        public Builder<I> defaultDurability(int durability) {
            properties.defaultDurability(durability);
            return this;
        }

        public Builder<I> durability(int durability) {
            properties.durability(durability);
            return this;
        }

        public Builder<I> craftRemainder(ItemLike itemLike) {
            properties.craftRemainder(itemLike.asItem());
            return this;
        }

        public Builder<I> creativeTab(CreativeModeTab tab) {
            properties.tab(tab);
            return this;
        }

        public Builder<I> rarity(Rarity rarity) {
            properties.rarity(rarity);
            return this;
        }

        public Builder<I> fireResistant() {
            properties.fireResistant();
            return this;
        }

        @SafeVarargs
        public final Builder<I> tags(TagKey<Item>... tags) {
            this.tags.addAll(Arrays.asList(tags));
            return this;
        }

        public Builder<I> simpleModel() {
            itemModelGenerators.add((e, provider) -> ModelTemplates.FLAT_HANDHELD_ITEM.create(
                ModelLocationUtils.getModelLocation(e.get()), TextureMapping.layer0(e.get()), provider.getModelConsumer()
            ));
            return this;
        }

        public Builder<I> model(ModelTemplate template) {
            itemModelGenerators.add((e, provider) -> template.create(e.getId(),
                TextureMapping.layer0(e.get()),
                provider.getModelConsumer()));
            return this;
        }

        public Builder<I> model(BiConsumer<RegistryEntry<I>, ItemModelProvider> model) {
            itemModelGenerators.add(model);
            return this;
        }

        public Builder<I> defaultLang(String englishName) {
            defaultLang = englishName;
            return this;
        }

        public Builder<I> noLang() {
            noLang = true;
            return this;
        }

        private String generateDefaultLang() {
            return Objects.requireNonNullElseGet(defaultLang, () -> AlmostUtils.capitalizeWords(id.replace('_', ' ')));
        }

        public ItemEntry<I> register() {
            final CreativeModeTab tab = ((ItemPropertiesAccessor) properties).getCreativeTab();
            final CreativeModeTab defaultTab = ItemRegistration.this.getDefaultCreativeTab();
            if (defaultTab != null && tab == null) {
                properties.tab(defaultTab);
            }
            ItemEntry<I> item = ItemRegistration.this.register(id, () -> factory.apply(properties));

            applyDataGen(dg -> {
                if (!noLang) {
                    dg.common().lang(p -> p.addLang(item.get().getDescriptionId(), generateDefaultLang()));
                }
                dg.common().itemModel(p -> itemModelGenerators.forEach(c -> c.accept(item, p)));
                dg.platform().tags(Registry.ITEM, p -> tags.forEach(t -> p.tag(t).add(item.get())));
            });

            return item;
        }
    }
}
