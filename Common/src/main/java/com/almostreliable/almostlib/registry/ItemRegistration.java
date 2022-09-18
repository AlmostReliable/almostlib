package com.almostreliable.almostlib.registry;

import com.almostreliable.almostlib.datagen.DataGenHolder;
import com.almostreliable.almostlib.datagen.DataGenManager;
import com.almostreliable.almostlib.datagen.provider.ItemModelProvider;
import com.almostreliable.almostlib.item.LazyCreativeTab;
import com.almostreliable.almostlib.mixin.ItemPropertiesAccessor;
import com.almostreliable.almostlib.util.AlmostUtils;
import net.minecraft.core.Registry;
import net.minecraft.data.models.model.ModelTemplate;
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

    @Nullable private LazyCreativeTab lazyCreativeTab;
    private DataGenManager dataGenManager;

    ItemRegistration(String namespace, Registry<Item> registry) {
        super(namespace, registry);
    }

    @Override
    protected ItemEntry<? extends Item> createEntry(ResourceLocation id, Supplier<? extends Item> supplier) {
        return new ItemEntry<>(getRegistry(), id, AlmostUtils.cast(supplier));
    }

    /**
     * Initialize a creative tab with given description. By default, the creative tab has no icon.
     * By using the builder when creating a new item use {@link Builder#markAsDefaultCreativeTab()} to set the tab icon.
     * Using {@link Builder#markAsDefaultCreativeTab()} multiple times will throw an exception.
     *
     * @param description The description of the creative tab.
     * @return self
     */
    public ItemRegistration initDefaultCreativeTab(String description) {
        String translationKey = description.toLowerCase(Locale.ENGLISH).replaceAll("\\s+", "_");
        ResourceLocation location = new ResourceLocation(getNamespace(), translationKey);
        lazyCreativeTab = new LazyCreativeTab(location);
        return this;
    }

    @Nullable
    public CreativeModeTab getDefaultCreativeTab() {
        if (lazyCreativeTab == null) {
            return null;
        }
        return lazyCreativeTab.getTab();
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T extends Item> ItemEntry<T> register(String name, Supplier<? extends T> supplier) {
        return (ItemEntry<T>) super.register(name, supplier);
    }

    public <I extends Item> Builder<I> builder(String name, Function<Item.Properties, ? extends I> factory) {
        return new Builder<>(name, factory);
    }

    public Builder<Item> builder(String name) {
        return new Builder<>(name, Item::new);
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
        private final String name;
        private Item.Properties properties;

        private final List<BiConsumer<RegistryEntry<I>, ItemModelProvider>> itemModelGenerators = new ArrayList<>();

        private final Set<TagKey<Item>> tags = new HashSet<>();

        private boolean isDefaultCreativeTabIcon = false;

        public Builder(String name, Function<Item.Properties, ? extends I> factory) {
            this.name = name;
            this.factory = factory;
            this.properties = new Item.Properties();
        }

        private Builder<I> markAsDefaultCreativeTab() {
            if (ItemRegistration.this.lazyCreativeTab == null) {
                throw new IllegalStateException("No default creative tab has been set in the ItemRegistration");
            }

            if (ItemRegistration.this.lazyCreativeTab.hasItem()) {
                throw new IllegalStateException("Default creative tab has already been set");
            }

            this.isDefaultCreativeTabIcon = true;
            return this;
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

        public ItemEntry<I> register() {
            final CreativeModeTab tab = ((ItemPropertiesAccessor) properties).getCreativeTab();
            final CreativeModeTab defaultTab = ItemRegistration.this.getDefaultCreativeTab();
            if (defaultTab != null && tab == null) {
                properties.tab(defaultTab);
            }
            ItemEntry<I> item = ItemRegistration.this.register(name, () -> factory.apply(properties));

            if (ItemRegistration.this.lazyCreativeTab != null && isDefaultCreativeTabIcon) {
                ItemRegistration.this.lazyCreativeTab.setItem(item.asStack());
            }

            applyDataGen(dg -> {
                dg.common().itemModel(p -> itemModelGenerators.forEach(c -> c.accept(item, p)));
                dg.platform().tags(Registry.ITEM, p -> tags.forEach(t -> p.tag(t).add(item.get())));
            });

            return item;
        }
    }
}
