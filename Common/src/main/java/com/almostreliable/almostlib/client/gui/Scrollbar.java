package com.almostreliable.almostlib.client.gui;

import com.mojang.blaze3d.vertex.PoseStack;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.util.Mth;

@Environment(EnvType.CLIENT)
public class Scrollbar implements GuiEventListener {

    private final WidgetData data;
    private int value;
    private int sliderHeight = 15;
    private int maxScrollableValue = Integer.MAX_VALUE;
    private boolean dragging;
    private int dragOffset;
    private int scrollFactor = 10;
    private boolean snapOnScrollFactor = false;
    private boolean hoveredSlider = false;

    public Scrollbar(int x, int y, int width, int height) {
        this.data = WidgetData.of(x, y, width, height);
    }

    public void updateHovered(int mouseX, int mouseY) {
        hoveredSlider = isMouseOverSlider(mouseX, mouseY);
        getData().setHovered(isMouseOver(mouseX, mouseY));
    }

    public boolean isHoveredSlider() {
        return hoveredSlider;
    }

    public boolean isHovered() {
        return getData().isHovered();
    }

    public int getSliderY() {
        if (maxScrollableValue == 0) {
            return getData().getY();
        }

        return (getData().getHeight() - getSliderHeight()) * value / maxScrollableValue + getData().getY();
    }

    public int getSliderHeight() {
        return sliderHeight;
    }

    public int getValue() {
        return value;
    }

    public void setMaxScrollableValue(int height) {
        this.maxScrollableValue = Math.max(0, height);
    }

    public void setSliderHeight(int height) {
        this.sliderHeight = Mth.clamp(height, 15, getData().getHeight()) - 1;
    }

    public void setScrollFactor(int scrollFactor) {
        this.scrollFactor = scrollFactor;
    }

    public void snapOnScrollFactor(boolean snap) {
        this.snapOnScrollFactor = snap;
    }

    private int getScrollFactor() {
        return scrollFactor;
    }

    public void setValue(double value) {
        this.value = (int) Mth.clamp(value, 0d, maxScrollableValue);
        if (shouldSnap()) {
            this.value = this.value - this.value % scrollFactor;
        }
    }

    public boolean shouldSnap() {
        return snapOnScrollFactor && !Screen.hasShiftDown();
    }

    public boolean isMouseOverSlider(double mouseX, double mouseY) {
        return getData().getX() <= mouseX && mouseX <= getData().getRight() && getSliderY() <= mouseY && mouseY <= getSliderY() + getSliderHeight();
    }

    protected void activateDragging(double mouseY) {
        dragging = true;
        dragOffset = (int) (mouseY - getSliderY());
    }

    protected void deactivateDragging() {
        dragging = false;
        dragOffset = 0;
    }

    protected void updateValueByMouse(double mouseY, double offset) {
        double mappedMouseY = mapToScrollValue(mouseY - getData().getY());
        double mappedOffset = mapToScrollValue(offset);
        setValue(mappedMouseY - mappedOffset);
    }

    protected double mapToScrollValue(double v) {
        return (v * maxScrollableValue / (getData().getHeight() - getSliderHeight()));
    }

    public WidgetData getData() {
        return data;
    }

    @Override
    public boolean isMouseOver(double mouseX, double mouseY) {
        return getData().isActive() && getData().isVisible() && getData().inBounds(mouseX, mouseY);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double f) {
        setValue(value - f * getScrollFactor());
        return true;
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int mouseButton, double dragX, double dragY) {
        if (mouseButton == 0 && dragging) {
            updateValueByMouse(mouseY, dragOffset);
            return true;
        }
        deactivateDragging();
        return false;
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int mouseButton) {
        if (mouseButton != 0 || !isMouseOver(mouseX, mouseY)) {
            deactivateDragging();
            return GuiEventListener.super.mouseClicked(mouseX, mouseY, mouseButton);
        }

        if (!isHoveredSlider()) {
            updateValueByMouse(mouseY, getSliderHeight() / 2d);
        }

        activateDragging(mouseY);
        return true;
    }

    public void renderDebug(PoseStack poseStack) {
        var sliderY = getSliderY();
        GuiComponent.fill(poseStack, getData().getX(), getData().getY(), getData().getRight(), getData().getBottom(), 0x80_000FFF);
        if (isHovered()) {
            GuiComponent.fill(poseStack, getData().getX(), getData().getY(), getData().getRight(), getData().getY() + 3, 0xC0_340BA3);
        }
        GuiComponent.fill(poseStack, getData().getX(), sliderY, getData().getRight(), sliderY + getSliderHeight(), 0xA0_FF0000);
        if (isHoveredSlider()) {
            GuiComponent.fill(poseStack, getData().getX(), sliderY, getData().getRight(), sliderY + 3, 0xC0_d7db00);
        }
        String s = String.format("%d/%d", value, maxScrollableValue);
        GuiComponent.drawString(poseStack, Minecraft.getInstance().font, s, getData().getRight() + 1, getData().getY(), 0xFF_FF0000);
    }
}
