package com.almostreliable.almostlib.datagen.blockstate;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.data.models.blockstates.BlockStateGenerator;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

import java.util.LinkedHashSet;
import java.util.Set;

public class VariantGenerator implements BlockStateGenerator {

    private final Set<PartialBlockStateModel> models = new LinkedHashSet<>();
    private final Block block;

    public VariantGenerator(Block block) {
        this.block = block;
    }

    public boolean hasModelForState(BlockState state) {
        return models.stream().anyMatch(model -> model.test(state));
    }

    public void add(PartialBlockStateModel model) {
        models.add(model);
    }

    @Override
    public JsonElement get() {
        JsonObject json = new JsonObject();
        JsonObject variants = new JsonObject();
        for (PartialBlockStateModel model : models) {
            String key = model.propertiesToString();
            variants.add(key, model.get());
        }
        json.add("variants", variants);
        return json;
    }

    @Override
    public Block getBlock() {
        return block;
    }
}
