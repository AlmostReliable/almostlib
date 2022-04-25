package com.github.almostreliable.lib.datagen;

import com.github.almostreliable.lib.registry.RegistryEntry;
import net.minecraft.data.DataGenerator;

import java.util.function.BiConsumer;

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
    }

    public static class Entry<T> {
        private final RegistryEntry<T> registryEntry;
        private final BiConsumer<RegistryEntry<T>, DataGenManager> callback;

        public Entry(RegistryEntry<T> registryEntry, BiConsumer<RegistryEntry<T>, DataGenManager> callback) {
            this.registryEntry = registryEntry;
            this.callback = callback;
        }

        public void invoke(DataGenManager dataGenManager) {
            callback.accept(registryEntry, dataGenManager);
        }
    }

}
