package com.almostreliable.lib.client.rendering;

import com.almostreliable.lib.AlmostLibConstants;
import com.almostreliable.lib.mixin.RenderTypeAccessor;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.renderer.RenderType;

/**
 * A {@link RenderType} that delegates to another {@link RenderType} and allows
 * to run custom code before and after the delegate's setup and clear methods.
 */
@Environment(EnvType.CLIENT)
final class DelegateRenderType extends RenderType {

    private final RenderType delegate;

    DelegateRenderType(RenderType delegate, Runnable setup, Runnable clear) {
        super(
            String.format("%s_delegate", AlmostLibConstants.MOD_ID),
            delegate.format(),
            delegate.mode(),
            delegate.bufferSize(),
            delegate.affectsCrumbling(),
            ((RenderTypeAccessor) delegate).getSortOnUpload(),
            setup,
            clear
        );
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
        return "DelegateRenderType{delegate=" + delegate + "}";
    }
}
