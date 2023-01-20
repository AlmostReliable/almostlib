package com.almostreliable.almostlib.datagen.provider;

import com.google.gson.JsonElement;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.DataProvider;
import net.minecraft.resources.ResourceLocation;

import java.io.IOException;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Supplier;

public class ItemModelProvider extends AbstractDataProvider {
    private final Map<ResourceLocation, Supplier<JsonElement>> modelGenerators;

    public ItemModelProvider(String namespace, DataGenerator dataGenerator) {
        super(namespace, dataGenerator);
        modelGenerators = new HashMap<>();
    }

    @Override
    public void run(CachedOutput cachedOutput) throws IOException {
        for (var entry : modelGenerators.entrySet()) {
            Path modelPath = getModelPath(entry.getKey());
            DataProvider.saveStable(cachedOutput, entry.getValue().get(), modelPath);
        }
    }

    public void addModel(ResourceLocation resourceLocation, Supplier<JsonElement> supplier) {
        modelGenerators.put(resourceLocation, supplier);
    }

    public BiConsumer<ResourceLocation, Supplier<JsonElement>> getModelConsumer() {
        return modelGenerators::put;
    }
}
