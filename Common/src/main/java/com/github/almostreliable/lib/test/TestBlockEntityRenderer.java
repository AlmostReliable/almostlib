package com.github.almostreliable.lib.test;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class TestBlockEntityRenderer implements BlockEntityRenderer<TestBlockEntity> {

    public TestBlockEntityRenderer(BlockEntityRendererProvider.Context context) {

    }

    @Override
    public void render(TestBlockEntity var1, float var2, PoseStack var3, MultiBufferSource var4, int var5, int var6) {
        String s = "";
    }
}
