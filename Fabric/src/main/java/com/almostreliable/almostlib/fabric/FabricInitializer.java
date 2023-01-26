package com.almostreliable.almostlib.fabric;

import com.almostreliable.almostlib.datagen.template.LayeredModelTemplate;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;

public class FabricInitializer implements ModInitializer {

    @Override
    public void onInitialize() {
    }

    @Environment(EnvType.CLIENT)
    public static void initRenderType(ResourceLocation resourceLocation, LayeredModelTemplate.RenderLayer renderLayer) {
        Block block = Registry.BLOCK.get(resourceLocation);
        if (block.equals(Blocks.AIR)) return;
        BlockRenderLayerMap.INSTANCE.putBlock(block, renderLayer.renderType.get());
    }
}
