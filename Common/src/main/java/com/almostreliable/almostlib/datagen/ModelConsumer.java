package com.almostreliable.almostlib.datagen;

import com.google.gson.JsonElement;
import net.minecraft.data.models.model.ModelLocationUtils;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Supplier;
import java.util.function.UnaryOperator;

public class ModelConsumer implements BiConsumer<ResourceLocation, Supplier<JsonElement>> {

    private final Map<ResourceLocation, Supplier<JsonElement>> modelGenerators = new HashMap<>();

    @Override
    public void accept(ResourceLocation id, Supplier<JsonElement> jsonGenerator) {
        modelGenerators.put(id, jsonGenerator);
    }

    public void add(ResourceLocation id, Supplier<JsonElement> jsonGenerator) {
        modelGenerators.put(id, jsonGenerator);
    }

    public void decorate(Block block, UnaryOperator<Supplier<JsonElement>> decorator) {
        ResourceLocation modelLocation = ModelLocationUtils.getModelLocation(block);
        modelGenerators.computeIfPresent(modelLocation, (key, value) -> decorator.apply(value));
    }

    public void decorate(Item item, UnaryOperator<Supplier<JsonElement>> decorator) {
        ResourceLocation modelLocation = ModelLocationUtils.getModelLocation(item);
        modelGenerators.computeIfPresent(modelLocation, (key, value) -> decorator.apply(value));
    }

    public Map<ResourceLocation, Supplier<JsonElement>> getModelGenerators() {
        return modelGenerators;
    }
}
