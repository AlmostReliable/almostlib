package com.almostreliable.almostlib.client.gui;

import com.almostreliable.almostlib.client.rendering.AlmostPoseStack;
import com.almostreliable.almostlib.client.rendering.RenderElement;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarratableEntry;
import net.minecraft.client.gui.narration.NarrationElementOutput;

public interface TranslatableWidget<T extends AlmostWidget<?> & GuiEventListener> extends RenderElement, GuiEventListener, NarratableEntry {

    T getInnerWidget();

    double getTranslateX();

    double getTranslateY();

    default double calcMouseXTranslation(double mouseX) {
        return mouseX - getTranslateX();
    }

    default double calcMouseYTranslation(double mouseY) {
        return mouseY - getTranslateY();
    }

    @Override
    default void render(AlmostPoseStack poseStack, int mouseX, int mouseY, float delta) {
        poseStack.pushPose();
        poseStack.translate(getTranslateX(), getTranslateY(), 0);
        mouseX = (int) calcMouseXTranslation(mouseX);
        mouseY = (int) calcMouseYTranslation(mouseY);
        postRender(poseStack, mouseX, mouseY, delta);
        poseStack.popPose();
    }

    default void postRender(AlmostPoseStack poseStack, int mouseX, int mouseY, float delta) {
        getInnerWidget().render(poseStack, mouseX, mouseY, delta);
    }

    @Override
    default void mouseMoved(double mouseX, double mouseY) {
        mouseX = calcMouseXTranslation(mouseX);
        mouseY = calcMouseYTranslation(mouseY);
        getInnerWidget().mouseMoved(mouseX, mouseY);
    }

    @Override
    default boolean mouseClicked(double mouseX, double mouseY, int mouseButton) {
        mouseX = calcMouseXTranslation(mouseX);
        mouseY = calcMouseYTranslation(mouseY);
        return getInnerWidget().mouseClicked(mouseX, mouseY, mouseButton);
    }

    @Override
    default boolean mouseReleased(double mouseX, double mouseY, int mouseButton) {
        mouseX = calcMouseXTranslation(mouseX);
        mouseY = calcMouseYTranslation(mouseY);
        return getInnerWidget().mouseReleased(mouseX, mouseY, mouseButton);
    }

    @Override
    default boolean mouseDragged(double mouseX, double mouseY, int mouseButton, double dragX, double dragY) {
        // TODO need to translate the dragX and dragY too?
        mouseX = calcMouseXTranslation(mouseX);
        mouseY = calcMouseYTranslation(mouseY);
        return getInnerWidget().mouseDragged(mouseX, mouseY, mouseButton, dragX, dragY);
    }

    @Override
    default boolean mouseScrolled(double mouseX, double mouseY, double value) {
        mouseX = calcMouseXTranslation(mouseX);
        mouseY = calcMouseYTranslation(mouseY);
        return getInnerWidget().mouseScrolled(mouseX, mouseY, value);
    }

    @Override
    default boolean keyPressed(int key, int scancode, int mods) {
        return getInnerWidget().keyPressed(key, scancode, mods);
    }

    @Override
    default boolean keyReleased(int key, int scancode, int mods) {
        return getInnerWidget().keyReleased(key, scancode, mods);
    }

    @Override
    default boolean charTyped(char c, int mods) {
        return getInnerWidget().charTyped(c, mods);
    }

    @Override
    default boolean changeFocus(boolean bl) {
        return getInnerWidget().changeFocus(bl);
    }

    @Override
    default boolean isMouseOver(double mouseX, double mouseY) {
        mouseX = calcMouseXTranslation(mouseX);
        mouseY = calcMouseYTranslation(mouseY);
        return getInnerWidget().isMouseOver(mouseX, mouseY);
    }

    @Override
    default NarrationPriority narrationPriority() {
        if (getInnerWidget() instanceof NarratableEntry n) {
            return n.narrationPriority();
        }
        return NarrationPriority.NONE;
    }

    @Override
    default void updateNarration(NarrationElementOutput narrationElementOutput) {
        if (getInnerWidget() instanceof NarratableEntry n) {
            n.updateNarration(narrationElementOutput);
        }
    }
}
