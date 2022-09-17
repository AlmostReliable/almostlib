package com.almostreliable.almostlib.datagen;

import com.google.gson.JsonElement;
import net.minecraft.core.Registry;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.DataProvider;
import net.minecraft.data.HashCache;
import net.minecraft.data.models.blockstates.BlockStateGenerator;
import net.minecraft.resources.ResourceLocation;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Supplier;

public class BlockStateProvider extends AbstractDataProvider {
    private final List<BlockStateGenerator> blockStateGenerators;
    private final Map<ResourceLocation, Supplier<JsonElement>> modelGenerators;

    public BlockStateProvider(String modId, DataGenerator dataGenerator) {
        super(modId, dataGenerator);
        blockStateGenerators = new ArrayList<>();
        modelGenerators = new HashMap<>();
    }

    @Override
    public void run(HashCache var1) throws IOException {
        for (var generator : blockStateGenerators) {
            Path path = getBlockStatePath(generator);
            DataProvider.save(GSON, var1, generator.get(), path);
        }

        for (var entry : modelGenerators.entrySet()) {
            Path path = getModelPath(entry.getKey());
            DataProvider.save(GSON, var1, entry.getValue().get(), path);
        }
    }

    private Path getBlockStatePath(BlockStateGenerator blockStateGenerator) {
        ResourceLocation key = Registry.BLOCK.getKey(blockStateGenerator.getBlock());
        return getAssetsPath().resolve(key.getNamespace() + "/blockstates/" + key.getPath() + ".json");
    }

    private Path getModelPath(ResourceLocation resourceLocation) {
        return getAssetsPath().resolve(
                resourceLocation.getNamespace() + "/models/" + resourceLocation.getPath() + ".json");
    }

    public void addBlockState(BlockStateGenerator blockStateGenerator) {
        blockStateGenerators.add(blockStateGenerator);
    }

    public void addModel(ResourceLocation resourceLocation, Supplier<JsonElement> supplier) {
        modelGenerators.put(resourceLocation, supplier);
    }

    public BiConsumer<ResourceLocation, Supplier<JsonElement>> getModelConsumer() {
        return modelGenerators::put;
    }
}
