package com.almostreliable.lib.datagen.provider;

import com.almostreliable.lib.datagen.ModelConsumer;
import com.google.gson.JsonElement;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.DataProvider;
import net.minecraft.resources.ResourceLocation;

import java.io.IOException;
import java.nio.file.Path;
import java.util.function.BiConsumer;
import java.util.function.Supplier;

public class ItemModelProvider extends AbstractDataProvider {

    private final ModelConsumer modelConsumer;

    public ItemModelProvider(String namespace, DataGenerator dataGenerator) {
        super(namespace, dataGenerator);
        modelConsumer = new ModelConsumer();
    }

    @Override
    public void run(CachedOutput cachedOutput) throws IOException {
        for (var entry : modelConsumer.getModelGenerators().entrySet()) {
            Path modelPath = getModelPath(entry.getKey());
            DataProvider.saveStable(cachedOutput, entry.getValue().get(), modelPath);
        }
    }

    public void addModel(ResourceLocation resourceLocation, Supplier<JsonElement> supplier) {
        modelConsumer.accept(resourceLocation, supplier);
    }

    public BiConsumer<ResourceLocation, Supplier<JsonElement>> getModelConsumer() {
        return modelConsumer;
    }
}
