package com.almostreliable.almostlib.fabric;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;

public class FabricInitializer implements ModInitializer {

    @Override
    public void onInitialize() {
    }

    @Environment(EnvType.CLIENT)
    public static void initRenderType(ResourceLocation resourceLocation, RenderType renderType) {
        Block block = Registry.BLOCK.get(resourceLocation);
        if (block.equals(Blocks.AIR)) return;
        BlockRenderLayerMap.INSTANCE.putBlock(block, renderType);
    }
}
