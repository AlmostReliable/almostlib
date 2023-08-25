package com.almostreliable.lib.datagen;

import com.almostreliable.lib.datagen.provider.*;
import com.almostreliable.lib.util.AlmostUtils;
import net.minecraft.core.Registry;
import net.minecraft.data.DataGenerator;

import java.util.HashMap;
import java.util.Map;

public class DataGenProviders {

    private final BlockStateProvider blockStateProvider;
    private final LangProvider langProvider;
    private final ItemModelProvider itemModelProvider;
    private final LootTableProvider lootTableProvider;
    private final Map<Registry<?>, TagsProvider<?>> tagsProviderMap = new HashMap<>();
    private final String namespace;
    private final DataGenerator dataGenerator;

    public DataGenProviders(String namespace, DataGenerator dataGenerator) {
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
}
