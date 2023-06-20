package com.almostreliable.almostlib.datagen.template;

import com.almostreliable.almostlib.datagen.provider.BlockStateProvider;
import com.almostreliable.almostlib.registry.BlockEntry;
import com.almostreliable.almostlib.util.Rotation;
import net.minecraft.data.models.blockstates.MultiVariantGenerator;
import net.minecraft.data.models.blockstates.Variant;
import net.minecraft.data.models.blockstates.VariantProperties;
import net.minecraft.data.models.model.ModelTemplates;
import net.minecraft.data.models.model.TextureMapping;
import net.minecraft.data.models.model.TextureSlot;
import net.minecraft.data.models.model.TexturedModel;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;

// TODO: more templates
public class BlockStateTemplates {

    public static <T extends Block> void simple(BlockEntry<T> entry, BlockStateProvider provider) {
        ResourceLocation modelLocation = TexturedModel.CUBE.create(entry.get(), provider.getModelConsumer());
        var variant = MultiVariantGenerator.multiVariant(entry.get(), Variant.variant().with(VariantProperties.MODEL, modelLocation));
        provider.addBlockState(variant);
    }

    public static <T extends Block> void horizontalFacing(BlockEntry<T> entry, BlockStateProvider provider) {
        Block block = entry.get();
        ResourceLocation modelLocation = getOrientableModel(provider, block);

        provider.createVariantFor(block,
            model -> model.model(modelLocation)
                .yRotation(Rotation.ofHorizontalFacing(model.getValue(BlockStateProperties.HORIZONTAL_FACING))),
            BlockStateProperties.HORIZONTAL_FACING);
    }

    public static <T extends Block> void facing(BlockEntry<T> entry, BlockStateProvider provider) {
        Block block = entry.get();
        ResourceLocation modelLocation = getOrientableModel(provider, block);

        provider.createVariantFor(block,
            model -> model.model(modelLocation)
                .yRotation(Rotation.ofHorizontalFacing(model.getValue(BlockStateProperties.FACING)))
                .xRotation(Rotation.ofVerticalFacing(model.getValue(BlockStateProperties.FACING))),
            BlockStateProperties.FACING);
    }

    private static ResourceLocation getOrientableModel(BlockStateProvider provider, Block block) {
        var mapping = new TextureMapping()
            .put(TextureSlot.FRONT, TextureMapping.getBlockTexture(block, "_front"))
            .put(TextureSlot.SIDE, TextureMapping.getBlockTexture(block))
            .put(TextureSlot.TOP, TextureMapping.getBlockTexture(block));
        return ModelTemplates.CUBE_ORIENTABLE.create(block, mapping, provider.getModelConsumer());
    }
}
