package com.almostreliable.lib.client.rendering;

import com.almostreliable.lib.mixin.RenderBuffersAccessor;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.VertexConsumer;
import it.unimi.dsi.fastutil.objects.Object2ObjectLinkedOpenHashMap;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderBuffers;
import net.minecraft.client.renderer.RenderType;

import java.util.Map;
import java.util.function.Consumer;

/**
 * A custom {@link MultiBufferSource.BufferSource} that allows to render with a custom alpha value.
 */
public final class AlphaBufferSource extends MultiBufferSource.BufferSource {

    public static final AlphaBufferSource INSTANCE = create(Minecraft.getInstance().renderBuffers());

    private final Map<RenderType, RenderType> delegatedRenderTypes = new Object2ObjectLinkedOpenHashMap<>();
    private float alpha = 1f;

    private static AlphaBufferSource create(RenderBuffers renderBuffers) {
        AlphaBufferSource alphaBufferSource = new AlphaBufferSource(new BufferBuilder(256));
        var fb = ((RenderBuffersAccessor) renderBuffers).getFixedBuffers();
        for (var e : fb.entrySet()) {
            DelegateRenderType rt = new DelegateRenderType(e.getKey(), alphaBufferSource::onShaderSetup, alphaBufferSource::onShaderClear);
            alphaBufferSource.fixedBuffers.put(rt, e.getValue());
            alphaBufferSource.delegatedRenderTypes.put(e.getKey(), rt);
        }
        return alphaBufferSource;
    }

    private AlphaBufferSource(BufferBuilder defaultBufferBuilder) {
        super(defaultBufferBuilder, new Object2ObjectLinkedOpenHashMap<>());
    }

    /**
     * Sets the alpha value for the next render call.
     * <p>
     * The value must be between 0 and 1.
     *
     * @param alpha The alpha value to apply.
     */
    public void setAlpha(float alpha) {
        this.alpha = alpha;
    }

    /**
     * Resets the alpha value back to 1.
     */
    public void resetAlpha() {
        this.alpha = 1f;
    }

    /**
     * Renders the given runnable with the passed alpha value.
     * <p>
     * After the runnable has been executed, the batch is ended and the alpha value is reset.
     *
     * @param alpha The alpha value to apply.
     * @param runnable The runnable to execute.
     */
    public void renderWithAlpha(float alpha, Runnable runnable) {
        setAlpha(alpha);
        runnable.run();
        endBatch();
        resetAlpha();
    }

    /**
     * Renders the given consumer with the passed alpha value.
     * <p>
     * After the consumer has been executed, the batch is ended and the alpha value is reset.
     *
     * @param alpha The alpha value to apply.
     * @param consumer The consumer to execute.
     */
    public void renderWithAlpha(float alpha, Consumer<AlphaBufferSource> consumer) {
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
        if (renderType instanceof DelegateRenderType) {
            return super.getBuffer(renderType);
        }

        RenderType art = delegatedRenderTypes.computeIfAbsent(renderType, rt -> new DelegateRenderType(rt, this::onShaderSetup, this::onShaderClear));
        return super.getBuffer(art);
    }
}
