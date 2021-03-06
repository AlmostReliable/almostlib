package com.almostreliable.lib.datagen;

import com.almostreliable.lib.Utils;
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
    private final String modId;
    private final DataGenerator dataGenerator;

    public DataGenManager(String modID, DataGenerator dataGenerator) {
        this.modId = modID;
        this.dataGenerator = dataGenerator;
        this.blockStateProvider = new BlockStateProvider(modID, dataGenerator);
        this.langProvider = new LangProvider(modID, dataGenerator);
        this.itemModelProvider = new ItemModelProvider(modID, dataGenerator);
        this.lootTableProvider = new LootTableProvider(modID, dataGenerator);
    }

    public <T> TagsProvider<T> getTagsProvider(Registry<T> registry) {
        return Utils.cast(tagsProviderMap.computeIfAbsent(registry, r -> new TagsProvider<>(modId, dataGenerator, r)));
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
