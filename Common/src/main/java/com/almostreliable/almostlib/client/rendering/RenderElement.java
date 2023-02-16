package com.almostreliable.almostlib.client.rendering;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public interface RenderElement {

    void render(AlmostPoseStack stack, int mouseX, int mouseY, float delta);
}
