package com.almostreliable.lib.datagen.provider;

import com.almostreliable.lib.datagen.ModelConsumer;
import com.almostreliable.lib.datagen.blockstate.PartialBlockStateModel;
import com.almostreliable.lib.datagen.blockstate.VariantGenerator;
import com.google.gson.JsonElement;
import net.minecraft.core.Registry;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.DataProvider;
import net.minecraft.data.models.blockstates.BlockStateGenerator;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;
import org.apache.commons.lang3.ArrayUtils;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.function.Supplier;

public class BlockStateProvider extends AbstractDataProvider {

    private final List<BlockStateGenerator> blockStateGenerators;
    private final ModelConsumer modelConsumer;

    public BlockStateProvider(String namespace, DataGenerator dataGenerator) {
        super(namespace, dataGenerator);
        blockStateGenerators = new ArrayList<>();
        modelConsumer = new ModelConsumer();
    }

    @Override
    public void run(CachedOutput cachedOutput) throws IOException {
        for (var generator : blockStateGenerators) {
            Path path = getBlockStatePath(generator);
            JsonElement json = generator.get();
            DataProvider.saveStable(cachedOutput, json, path);
        }

        for (var entry : modelConsumer.getModelGenerators().entrySet()) {
            Path path = getModelPath(entry.getKey());
            JsonElement json = entry.getValue().get();
            DataProvider.saveStable(cachedOutput, json, path);
        }
    }

    public void addBlockState(BlockStateGenerator blockStateGenerator) {
        Block block = blockStateGenerator.getBlock();
        if (blockStateGenerators.stream().anyMatch(g -> g.getBlock() == block)) {
            throw new IllegalStateException("BlockStateGenerator already exists for block " + Registry.BLOCK.getKey(block));
        }

        blockStateGenerators.add(blockStateGenerator);
    }

    public void addModel(ResourceLocation resourceLocation, Supplier<JsonElement> supplier) {
        modelConsumer.accept(resourceLocation, supplier);
    }

    public void createVariant(Block block, Consumer<PartialBlockStateModel> consumer) {
        createVariantExcept(block, consumer);
    }

    public void createVariantExcept(Block block, Consumer<PartialBlockStateModel> consumer, Property<?>... exceptProperties) {
        createVariant(block, consumer, p -> !ArrayUtils.contains(exceptProperties, p));
    }

    public void createVariantFor(Block block, Consumer<PartialBlockStateModel> consumer, Property<?>... includedProperties) {
        createVariant(block, consumer, p -> ArrayUtils.contains(includedProperties, p));
    }

    public void createVariant(Block block, Consumer<PartialBlockStateModel> consumer, Predicate<Property<?>> propertyFilter) {
        VariantGenerator generator = new VariantGenerator(block);
        for (BlockState state : block.getStateDefinition().getPossibleStates()) {
            if (generator.hasModelForState(state)) continue;
            PartialBlockStateModel model = PartialBlockStateModel.create(state, propertyFilter);
            consumer.accept(model);
            generator.add(model);
        }
        addBlockState(generator);
    }

    private Path getBlockStatePath(BlockStateGenerator blockStateGenerator) {
        ResourceLocation key = Registry.BLOCK.getKey(blockStateGenerator.getBlock());
        return getAssetsPath().resolve(key.getNamespace() + "/blockstates/" + key.getPath() + ".json");
    }

    public ModelConsumer getModelConsumer() {
        return modelConsumer;
    }
}
