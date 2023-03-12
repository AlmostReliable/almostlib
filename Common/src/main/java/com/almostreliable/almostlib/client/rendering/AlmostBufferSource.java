package com.almostreliable.almostlib.client.rendering;

import com.almostreliable.almostlib.mixin.RenderBuffersAccessor;
import com.almostreliable.almostlib.mixin.RenderTypeAccessor;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.VertexConsumer;
import it.unimi.dsi.fastutil.objects.Object2ObjectLinkedOpenHashMap;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderBuffers;
import net.minecraft.client.renderer.RenderType;

import java.util.Map;
import java.util.function.Consumer;

public class AlmostBufferSource extends MultiBufferSource.BufferSource {

    public static final AlmostBufferSource INSTANCE = create(Minecraft.getInstance().renderBuffers());

    private final Map<RenderType, RenderType> vanillaToAlmostRenderTypes = new Object2ObjectLinkedOpenHashMap<>();
    private float alpha = 1f;

    private static AlmostBufferSource create(RenderBuffers renderBuffers) {
        AlmostBufferSource almostBufferSource = new AlmostBufferSource(new BufferBuilder(256));
        var fb = ((RenderBuffersAccessor) renderBuffers).getFixedBuffers();
        for (var e : fb.entrySet()) {
            AlmostRenderType rt = new AlmostRenderType(e.getKey(), almostBufferSource::onShaderSetup, almostBufferSource::onShaderClear);
            almostBufferSource.fixedBuffers.put(rt, e.getValue());
            almostBufferSource.vanillaToAlmostRenderTypes.put(e.getKey(), rt);
        }
        return almostBufferSource;
    }

    private AlmostBufferSource(BufferBuilder defaultBufferBuilder) {
        super(defaultBufferBuilder, new Object2ObjectLinkedOpenHashMap<>());
    }

    public void setAlpha(float alpha) {
        this.alpha = alpha;
    }

    public void resetAlpha() {
        this.alpha = 1f;
    }

    public void renderWithAlpha(float alpha, Runnable runnable) {
        setAlpha(alpha);
        runnable.run();
        endBatch();
        resetAlpha();
    }

    public void renderWithAlpha(float alpha, Consumer<AlmostBufferSource> consumer) {
        setAlpha(alpha);
        consumer.accept(this);
        endBatch();
        resetAlpha();
    }

    private void onShaderSetup() {
        if (alpha != 1f) {
            RenderSystem.enableBlend();
            RenderSystem.setShaderColor(1, 1, 1, alpha);
        }
    }

    private void onShaderClear() {
        if (alpha != 1f) {
            RenderSystem.disableBlend();
            RenderSystem.setShaderColor(1, 1, 1, 1);
        }
    }

    @Override
    public VertexConsumer getBuffer(RenderType renderType) {
        if (renderType instanceof AlmostRenderType) {
            return super.getBuffer(renderType);
        }

        RenderType art = vanillaToAlmostRenderTypes.computeIfAbsent(renderType, (rt) -> new AlmostRenderType(rt, this::onShaderSetup, this::onShaderClear));
        return super.getBuffer(art);
    }

    @Environment(EnvType.CLIENT)
    private static class AlmostRenderType extends RenderType {

        private final RenderType delegate;

        public AlmostRenderType(RenderType delegate, Runnable setup, Runnable clear) {
            super("almost_render_type", delegate.format(), delegate.mode(), delegate.bufferSize(), delegate.affectsCrumbling(), ((RenderTypeAccessor) delegate).getSortOnUpload(), setup, clear);
            this.delegate = delegate;
        }

        @Override
        public void setupRenderState() {
            delegate.setupRenderState();
            super.setupRenderState();
        }

        @Override
        public void clearRenderState() {
            delegate.clearRenderState();
            super.clearRenderState();
        }

        @Override
        public String toString() {
            return "AlmostRenderType{delegate=" + delegate + "}";
        }
    }
}
