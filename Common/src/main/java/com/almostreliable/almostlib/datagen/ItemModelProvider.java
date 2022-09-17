package com.almostreliable.almostlib.datagen;

import com.google.gson.JsonElement;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.DataProvider;
import net.minecraft.data.HashCache;
import net.minecraft.resources.ResourceLocation;

import java.io.IOException;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Supplier;

public class ItemModelProvider extends AbstractDataProvider {
    private final Map<ResourceLocation, Supplier<JsonElement>> modelGenerators;

    public ItemModelProvider(String modId, DataGenerator dataGenerator) {
        super(modId, dataGenerator);
        modelGenerators = new HashMap<>();
    }

    @Override
    public void run(HashCache var1) throws IOException {
        for (var entry : modelGenerators.entrySet()) {
            Path modelPath = getModelPath(entry.getKey());
            DataProvider.save(GSON, var1, entry.getValue().get(), modelPath);
        }
    }

    private Path getModelPath(ResourceLocation resourceLocation) {
        return getAssetsPath().resolve(
                resourceLocation.getNamespace() + "/models/item/" + resourceLocation.getPath() + ".json");
    }

    public void addModel(ResourceLocation resourceLocation, Supplier<JsonElement> supplier) {
        modelGenerators.put(resourceLocation, supplier);
    }

    public BiConsumer<ResourceLocation, Supplier<JsonElement>> getModelConsumer() {
        return modelGenerators::put;
    }
}
