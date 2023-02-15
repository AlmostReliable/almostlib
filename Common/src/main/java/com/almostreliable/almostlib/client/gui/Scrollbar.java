package com.almostreliable.almostlib.client.gui;

import com.almostreliable.almostlib.client.rendering.AlmostPoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.util.Mth;

public class Scrollbar implements GuiEventListener, AlmostWidget<WidgetData> {

    private final WidgetData data;
    private int value;
    private int sliderHeight = 15;
    private int maxScrollableValue = Integer.MAX_VALUE;
    private boolean dragging;
    private int dragOffset;
    private int scrollFactor = 10;
    private int valueSnap = 0;

    public Scrollbar(int x, int y, int width, int height) {
        this.data = WidgetData.of(x, y, width, height);
    }

    @Override
    public void render(AlmostPoseStack poseStack, int mouseX, int mouseY, float delta) {
        getData().setHovered(isMouseOverSlider(mouseX, mouseY));
        GuiComponent.fill(poseStack, getData().getX(), getData().getY(), getData().getRight(), getData().getBottom(), 0x80_0000FF);
        GuiComponent.fill(poseStack, getData().getX(), getSliderY(), getData().getRight(), getSliderY() + getSliderHeight(), 0x80_FF0000);
        GuiComponent.drawString(poseStack, Minecraft.getInstance().font, String.valueOf(value), getData().getX() + 11, getData().getY(), 0xFF_FF0000);
        GuiComponent.drawString(poseStack, Minecraft.getInstance().font, String.valueOf(dragOffset), getData().getX() + 11, getData().getY() + Minecraft.getInstance().font.lineHeight, 0xFF_FF0000);
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

    public void setValueSnap(int valueSnap) {
        this.valueSnap = valueSnap;
    }

    private int getScrollFactor() {
        if(valueSnap > 0) {
            return valueSnap;
        }
        return scrollFactor;
    }

    public void setValue(double value) {
        this.value = (int) Mth.clamp(value, 0d, maxScrollableValue);
        if (valueSnap > 0) {
            this.value = this.value - this.value % valueSnap;
        }
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
        double mappedMouseY = mapPixelToScrollValue(mouseY - getData().getY());
        double mappedOffset = mapPixelToScrollValue(offset);
        setValue(mappedMouseY - mappedOffset);
    }

    protected double mapPixelToScrollValue(double v) {
        return (v * maxScrollableValue / (getData().getHeight() - getSliderHeight()));
    }

    @Override
    public WidgetData getData() {
        return data;
    }

    @Override
    public boolean isMouseOver(double mouseX, double mouseY) {
        return getData().inBounds(mouseX, mouseY);
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

        if (!getData().isHovered()) {
            updateValueByMouse(mouseY, getSliderHeight() / 2d);
        }

        activateDragging(mouseY);
        return true;
    }
}
