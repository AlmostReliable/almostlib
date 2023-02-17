package com.almostreliable.almostlib.client.gui.widget.composite;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.components.events.GuiEventListener;

/**
 * Base composite widget to apply custom translation before rendering. This is useful for things like scrolling containers. You can see an example of this in {@link ScrollableCompositeWidget}.
 * <p>
 * Interface delegates all {@link GuiEventListener} methods to the inner widget. It also translates the mouse coordinates before passing them to the inner widget.
 */
public abstract class TranslatableCompositeWidget extends CompositeWidget {

    public TranslatableCompositeWidget(int x, int y, int width, int height) {
        super(x, y, width, height);
    }

    public abstract double getTranslateX();

    public abstract double getTranslateY();

    public double calcMouseXTranslation(double mouseX) {
        return mouseX - getTranslateX();
    }

    public double calcMouseYTranslation(double mouseY) {
        return mouseY - getTranslateY();
    }

    @Override
    public void renderWidgets(PoseStack poseStack, int mouseX, int mouseY, float delta) {
        poseStack.pushPose();
        poseStack.translate(getTranslateX(), getTranslateY(), 0);
        mouseX = (int) calcMouseXTranslation(mouseX);
        mouseY = (int) calcMouseYTranslation(mouseY);
        super.renderWidgets(poseStack, mouseX, mouseY, delta);
        poseStack.popPose();
    }

    @Override
    public void mouseMoved(double mouseX, double mouseY) {
        mouseX = calcMouseXTranslation(mouseX);
        mouseY = calcMouseYTranslation(mouseY);
        super.mouseMoved(mouseX, mouseY);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int mouseButton) {
        mouseX = calcMouseXTranslation(mouseX);
        mouseY = calcMouseYTranslation(mouseY);
        return super.mouseClicked(mouseX, mouseY, mouseButton);
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int mouseButton) {
        mouseX = calcMouseXTranslation(mouseX);
        mouseY = calcMouseYTranslation(mouseY);
        return super.mouseReleased(mouseX, mouseY, mouseButton);
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int mouseButton, double dragX, double dragY) {
        mouseX = calcMouseXTranslation(mouseX);
        mouseY = calcMouseYTranslation(mouseY);
        return super.mouseDragged(mouseX, mouseY, mouseButton, dragX, dragY);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double value) {
        mouseX = calcMouseXTranslation(mouseX);
        mouseY = calcMouseYTranslation(mouseY);
        return super.mouseScrolled(mouseX, mouseY, value);
    }

    @Override
    public boolean isMouseOver(double mouseX, double mouseY) {
        mouseX = calcMouseXTranslation(mouseX);
        mouseY = calcMouseYTranslation(mouseY);
        return super.isMouseOver(mouseX, mouseY);
    }
}
