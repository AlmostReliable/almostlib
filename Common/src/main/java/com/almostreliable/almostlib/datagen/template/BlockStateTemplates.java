package com.almostreliable.almostlib.datagen.template;

import com.almostreliable.almostlib.datagen.provider.BlockStateProvider;
import com.almostreliable.almostlib.registry.BlockEntry;
import net.minecraft.core.Direction;
import net.minecraft.data.models.blockstates.MultiVariantGenerator;
import net.minecraft.data.models.blockstates.PropertyDispatch;
import net.minecraft.data.models.blockstates.Variant;
import net.minecraft.data.models.blockstates.VariantProperties;
import net.minecraft.data.models.model.ModelTemplates;
import net.minecraft.data.models.model.TextureMapping;
import net.minecraft.data.models.model.TextureSlot;
import net.minecraft.data.models.model.TexturedModel;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;

import java.util.function.BiConsumer;

// TODO: more templates
public class BlockStateTemplates {

    public static <B extends Block> BiConsumer<BlockEntry<B>, BlockStateProvider> simple() {
        return (e, provider) -> {
            ResourceLocation simpleModel = TexturedModel.CUBE.create(e.get(), provider.getModelConsumer());
            var simpleVariant = MultiVariantGenerator.multiVariant(e.get(), Variant.variant().with(VariantProperties.MODEL, simpleModel));
            provider.addBlockState(simpleVariant);
        };
    }

    public static <B extends Block> BiConsumer<BlockEntry<B>, BlockStateProvider> withFacing() {
        return (e, provider) -> {
            var mapping = new TextureMapping()
                .put(TextureSlot.FRONT, TextureMapping.getBlockTexture(e.get(), "_front"))
                .put(TextureSlot.SIDE, TextureMapping.getBlockTexture(e.get()))
                .put(TextureSlot.TOP, TextureMapping.getBlockTexture(e.get()));
            var modelLocation = ModelTemplates.CUBE_ORIENTABLE.create(e.get(), mapping, provider.getModelConsumer());
            var facingProp = PropertyDispatch.property(BlockStateProperties.FACING)
                .select(Direction.DOWN, Variant.variant().with(VariantProperties.MODEL, modelLocation)
                    .with(VariantProperties.X_ROT, VariantProperties.Rotation.R90))
                .select(Direction.UP, Variant.variant().with(VariantProperties.MODEL, modelLocation)
                    .with(VariantProperties.X_ROT, VariantProperties.Rotation.R270))
                .select(Direction.NORTH, Variant.variant().with(VariantProperties.MODEL, modelLocation))
                .select(Direction.SOUTH, Variant.variant().with(VariantProperties.MODEL, modelLocation)
                    .with(VariantProperties.Y_ROT, VariantProperties.Rotation.R180))
                .select(Direction.WEST, Variant.variant().with(VariantProperties.MODEL, modelLocation)
                    .with(VariantProperties.Y_ROT, VariantProperties.Rotation.R270))
                .select(Direction.EAST, Variant.variant().with(VariantProperties.MODEL, modelLocation)
                    .with(VariantProperties.Y_ROT, VariantProperties.Rotation.R90));
            MultiVariantGenerator variant = MultiVariantGenerator.multiVariant(e.get()).with(facingProp);
            provider.addBlockState(variant);
        };
    }
}
