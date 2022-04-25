package com.github.almostreliable.lib.datagen;

import net.minecraft.data.DataGenerator;

public class DataGenManager {
    private final BlockStateProvider blockStateProvider;
    private final LangProvider langProvider;
    private final ItemModelProvider itemModelProvider;

    public DataGenManager(String modID, DataGenerator dataGenerator) {
        this.blockStateProvider = new BlockStateProvider(modID, dataGenerator);
        this.langProvider = new LangProvider(modID, dataGenerator);
        this.itemModelProvider = new ItemModelProvider(modID, dataGenerator);
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

    public void collectDataProvider(DataGenerator dataGenerator) {
        dataGenerator.addProvider(blockStateProvider);
        dataGenerator.addProvider(langProvider);
        dataGenerator.addProvider(itemModelProvider);
    }
}
