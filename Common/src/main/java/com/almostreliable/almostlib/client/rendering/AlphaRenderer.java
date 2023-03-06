package com.almostreliable.almostlib.client.rendering;

import com.almostreliable.almostlib.BuildConfig;
import com.almostreliable.almostlib.mixin.BufferSourceAccessor;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.VertexConsumer;
import it.unimi.dsi.fastutil.objects.Object2ObjectLinkedOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;

import java.util.IdentityHashMap;
import java.util.Map;

public final class AlphaRenderer {

    private static final float DEFAULT_ALPHA = 0.4f;

    private AlphaRenderer() {}

    public static MultiBufferSource.BufferSource wrapBuffer(MultiBufferSource.BufferSource buffer, float alpha) {
        BufferBuilder builder = ((BufferSourceAccessor) buffer).getBuilder();
        Map<RenderType, BufferBuilder> fixedBuffers = ((BufferSourceAccessor) buffer).getFixedBuffers();
        Map<RenderType, BufferBuilder> alphaBuffers = new Object2ObjectLinkedOpenHashMap<>();
        for (var e : fixedBuffers.entrySet()) {
            alphaBuffers.put(AlphaRenderType.of(e.getKey(), alpha), e.getValue());
        }
        return new AlphaBufferSource(builder, alphaBuffers, alpha);
    }

    public static MultiBufferSource.BufferSource wrapBuffer(MultiBufferSource.BufferSource buffer) {
        return wrapBuffer(buffer, DEFAULT_ALPHA);
    }

    private static final class AlphaBufferSource extends MultiBufferSource.BufferSource {

        private static final Map<RenderType, RenderType> CACHE = new IdentityHashMap<>();
        private final float alpha;

        private AlphaBufferSource(BufferBuilder buffer, Map<RenderType, BufferBuilder> buffers, float alpha) {
            super(buffer, buffers);
            this.alpha = alpha;
        }

        @Override
        public VertexConsumer getBuffer(RenderType type) {
            return super.getBuffer(CACHE.computeIfAbsent(type, t -> AlphaRenderType.of(t, alpha)));
        }
    }

    private static final class AlphaRenderType extends RenderType {

        private static final Map<Float, AlphaRenderType> CACHE = new Object2ObjectOpenHashMap<>();

        private final String id;
        private final RenderType delegate;
        private final float alpha;

        private AlphaRenderType(String id, RenderType delegate, float alpha) {
            super(String.format("%s_%s", id, alpha), delegate.format(), delegate.mode(), delegate.bufferSize(), delegate.affectsCrumbling(), true, () -> {}, () -> {});
            this.id = id;
            this.delegate = delegate;
            this.alpha = alpha;
        }

        private static RenderType of(RenderType type, float alpha) {
            if (type instanceof AlphaRenderType alphaType) {
                if (alphaType.alpha == alpha) {
                    return alphaType;
                }
                return CACHE.computeIfAbsent(alpha, alphaType::copyWith);
            }

            String id = String.format("%s_%s_alpha", BuildConfig.MOD_ID, type);
            return CACHE.computeIfAbsent(alpha, a -> new AlphaRenderType(id, type, a));
        }

        @Override
        public void setupRenderState() {
            delegate.setupRenderState();
            RenderSystem.disableDepthTest();
            RenderSystem.enableBlend();
            RenderSystem.setShaderColor(1, 1, 1, alpha);
        }

        @Override
        public void clearRenderState() {
            RenderSystem.setShaderColor(1, 1, 1, 1);
            RenderSystem.disableBlend();
            RenderSystem.enableDepthTest();
            delegate.clearRenderState();
        }

        private AlphaRenderType copyWith(float alpha) {
            return new AlphaRenderType(id, delegate, alpha);
        }
    }
}
