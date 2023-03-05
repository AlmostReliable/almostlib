package com.almostreliable.almostlib.client.gui.widget;

import com.almostreliable.almostlib.client.rendering.ScissorStack;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.components.Widget;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarratableEntry;
import net.minecraft.client.gui.narration.NarrationElementOutput;

/**
 * A wrapper widget to scale the rendering of another widget. The widget will translate the mouse coordinates before passing them to the wrapped widget.
 */
public class ScaleWidget implements Widget, GuiEventListener, NarratableEntry {

    private static final GuiEventListener EMPTY_LISTENER = new GuiEventListener() {
    };
    private final AlmostWidget<?> widget;
    private final GuiEventListener listener;
    private float scale = 1.0f;

    public ScaleWidget(AlmostWidget<?> widget) {
        this.widget = widget;
        this.listener = widget instanceof GuiEventListener listener ? listener : EMPTY_LISTENER;
    }

    public float getScale() {
        return scale;
    }

    public void setScale(float scale) {
        this.scale = scale;
    }

    public double calcMouseXTranslation(double mouseX) {
        return (mouseX - widget.getData().getX()) / getScale() + widget.getData().getX();
    }

    public double calcMouseYTranslation(double mouseY) {
        return (mouseY - widget.getData().getY()) / getScale() + widget.getData().getY();
    }

    @Override
    public void render(PoseStack poseStack, int mouseX, int mouseY, float delta) {
        ScissorStack.setRenderScale(getScale());
        poseStack.pushPose();
        {
            poseStack.translate(widget.getData().getX(), widget.getData().getY(), 0);
            poseStack.scale(getScale(), getScale(), 1);
            poseStack.translate(-widget.getData().getX(), -widget.getData().getY(), 0);
            mouseX = (int) calcMouseXTranslation(mouseX);
            mouseY = (int) calcMouseYTranslation(mouseY);
            widget.render(poseStack, mouseX, mouseY, delta); // 50 : 40
        }
        poseStack.popPose();
        ScissorStack.resetRenderScale();
    }

    @Override
    public void mouseMoved(double mouseX, double mouseY) {
        mouseX = calcMouseXTranslation(mouseX);
        mouseY = calcMouseYTranslation(mouseY);
        listener.mouseMoved(mouseX, mouseY);
    }

    @Override
    public boolean isMouseOver(double mouseX, double mouseY) {
        mouseX = calcMouseXTranslation(mouseX);
        mouseY = calcMouseYTranslation(mouseY);
        return listener.isMouseOver(mouseX, mouseY);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int mouseButton) {
        mouseX = calcMouseXTranslation(mouseX);
        mouseY = calcMouseYTranslation(mouseY);
        return listener.mouseClicked(mouseX, mouseY, mouseButton);
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int mouseButton) {
        mouseX = calcMouseXTranslation(mouseX);
        mouseY = calcMouseYTranslation(mouseY);
        return listener.mouseReleased(mouseX, mouseY, mouseButton);
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int mouseButton, double dragX, double dragY) {
        mouseX = calcMouseXTranslation(mouseX);
        mouseY = calcMouseYTranslation(mouseY);
        return listener.mouseDragged(mouseX, mouseY, mouseButton, dragX, dragY);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double value) {
        mouseX = calcMouseXTranslation(mouseX);
        mouseY = calcMouseYTranslation(mouseY);
        return listener.mouseScrolled(mouseX, mouseY, value);
    }

    @Override
    public NarrationPriority narrationPriority() {
        if (widget instanceof NarratableEntry entry) {
            return entry.narrationPriority();
        }
        return NarrationPriority.NONE;
    }

    @Override
    public void updateNarration(NarrationElementOutput narrationElementOutput) {
        if (widget instanceof NarratableEntry entry) {
            entry.updateNarration(narrationElementOutput);
        }
    }
}
