package com.almostreliable.almostlib.mixin;

import com.almostreliable.almostlib.AlmostLib;
import com.almostreliable.almostlib.client.rendering.AlmostRendering;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.ChatScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ChatScreen.class)
public final class ChatScreenMixin extends Screen {

    private ChatScreenMixin(Component component) {
        super(component);
    }

    @Inject(method = "init", at = @At("TAIL"))
    private void init(CallbackInfo ci) {
        if (AlmostLib.PLATFORM.isDevelopmentEnvironment()) {
            addRenderableWidget(
                new Button(
                    width - (100 + 4),
                    height - (20 + 14),
                    100,
                    20,
                    Component.literal("AlmostLib Debug"),
                    button -> AlmostRendering.toggleDebug(),
                    (button, poseStack, mouseX, mouseY) -> {
                        renderTooltip(poseStack, Component.literal("Toggle AlmostLib Debug"), mouseX, mouseY);
                    })
            );
        }
    }
}
