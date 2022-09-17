package com.almostreliable.almostlib.datagen;

import com.almostreliable.almostlib.util.AlmostUtils;
import net.minecraft.core.Registry;
import net.minecraft.data.DataGenerator;

import java.util.HashMap;
import java.util.Map;

public class DataGenManager {
    private final BlockStateProvider blockStateProvider;
    private final LangProvider langProvider;
    private final ItemModelProvider itemModelProvider;
    private final LootTableProvider lootTableProvider;
    private final Map<Registry<?>, TagsProvider<?>> tagsProviderMap = new HashMap<>();
    private final String namespace;
    private final DataGenerator dataGenerator;

    public DataGenManager(String namespace, DataGenerator dataGenerator) {
        this.namespace = namespace;
        this.dataGenerator = dataGenerator;
        this.blockStateProvider = new BlockStateProvider(namespace, dataGenerator);
        this.langProvider = new LangProvider(namespace, dataGenerator);
        this.itemModelProvider = new ItemModelProvider(namespace, dataGenerator);
        this.lootTableProvider = new LootTableProvider(namespace, dataGenerator);
    }

    public <T> TagsProvider<T> getTagsProvider(Registry<T> registry) {
        return AlmostUtils.cast(tagsProviderMap.computeIfAbsent(registry,
                r -> new TagsProvider<>(namespace, dataGenerator, r)));
    }

    public BlockStateProvider getBlockStateProvider() {
        return blockStateProvider;
    }

    public LangProvider getLangProvider() {
        return langProvider;
    }

    public ItemModelProvider getItemModelProvider() {
        return itemModelProvider;
    }

    public LootTableProvider getLootTableProvider() {
        return lootTableProvider;
    }

    public void collectDataProvider(DataGenerator dataGenerator) {
        dataGenerator.addProvider(blockStateProvider);
        dataGenerator.addProvider(langProvider);
        dataGenerator.addProvider(itemModelProvider);
        dataGenerator.addProvider(lootTableProvider);
        tagsProviderMap.forEach((registry, provider) -> dataGenerator.addProvider(provider));
    }
}
