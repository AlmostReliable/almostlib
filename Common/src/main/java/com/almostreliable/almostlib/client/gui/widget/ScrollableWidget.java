package com.almostreliable.almostlib.client.gui.widget;

import com.almostreliable.almostlib.client.gui.Padding;
import com.almostreliable.almostlib.client.gui.Scrollbar;
import com.almostreliable.almostlib.client.gui.WidgetData;
import com.almostreliable.almostlib.client.rendering.AlmostPoseStack;
import com.almostreliable.almostlib.util.Area;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.gui.components.Widget;
import net.minecraft.client.gui.components.events.GuiEventListener;

public class ScrollableWidget<T extends AlmostWidget<?> & GuiEventListener> implements TranslatableWidget<T>, Widget,
    AlmostWidget<WidgetData> {

    private final T content;
    private final WidgetData data;
    private Area contentArea;
    private final Scrollbar scrollbar;
    private boolean requiresScrollbarUpdate = true;

    public ScrollableWidget(T content, int x, int y, int width, int height) {
        this(content, x, y, width, height, Padding.of(3));
    }

    public ScrollableWidget(T content, int x, int y, int width, int height, Padding contentPadding) {
        this.content = content;
        this.data = WidgetData.of(x, y, width, height);
        this.scrollbar = createScrollbar();
        setContentPadding(contentPadding);
    }

    protected Scrollbar createScrollbar() {
        return new Scrollbar(getData().getRight() - 10, getData().getY() + 10, 10, getData().getHeight() - 20);
    }

    @Override
    public void render(AlmostPoseStack poseStack, int mouseX, int mouseY, float delta) {
        Font font = Minecraft.getInstance().font;
        GuiComponent.drawString(poseStack, font, getData().getHeight() + " (" + contentArea.getHeight() + ")", data.getX() - 11, data.getY() - font.lineHeight * 2, 0xFF_FF0000);
        GuiComponent.drawString(poseStack, font, String.valueOf(getContainerHeight()), data.getX() - 11, data.getY() - font.lineHeight, 0xFF_FF0000);
        GuiComponent.fill(poseStack, getData().getX(), getData().getY(), getData().getRight(), getData().getBottom(), 0x80_00FF00);

        renderContent(poseStack, mouseX, mouseY, delta);
        if (scrollbar.getData().isVisible()) {
            if (requiresScrollbarUpdate) {
                updateScrollbar();
                requiresScrollbarUpdate = false;
            }
            renderScrollbar(poseStack, mouseX, mouseY, delta);
        }
    }

    protected void renderContent(AlmostPoseStack poseStack, int mouseX, int mouseY, float delta) {
        GuiComponent.enableScissor(getContentArea().getX(), getContentArea().getY(), getContentArea().getRight(), getContentArea().getBottom());
        TranslatableWidget.super.render(poseStack, mouseX, mouseY, delta);
        GuiComponent.disableScissor();
    }

    protected void renderScrollbar(AlmostPoseStack poseStack, int mouseX, int mouseY, float delta) {
        scrollbar.updateHovered(mouseX, mouseY);
        if(poseStack.isDebug()) {
            scrollbar.renderDebug(poseStack);
        }
    }

    @Override
    public T getInnerWidget() {
        return content;
    }

    @Override
    public double getTranslateX() {
        return contentArea.getX();
    }

    @Override
    public double getTranslateY() {
        return contentArea.getY() - scrollbar.getValue();
    }

    @Override
    public double calcMouseXTranslation(double mouseX) {
        if (getContentArea().isHorizontalInside(mouseX)) {
            return TranslatableWidget.super.calcMouseXTranslation(mouseX);
        }
        return -Double.MAX_VALUE;
    }

    @Override
    public double calcMouseYTranslation(double mouseY) {
        if (getContentArea().isVerticalInside(mouseY)) {
            return TranslatableWidget.super.calcMouseYTranslation(mouseY);
        }
        return -Double.MAX_VALUE;
    }

    public void updateScrollbar() {
        scrollbar.setMaxScrollableValue(getContainerHeight() - contentArea.getHeight()); // TODO only update when content changes
        scrollbar.setSliderHeight(Math.min(contentArea.getHeight() * contentArea.getHeight() / getContainerHeight(), data.getHeight()));
    }

    public void setContentPadding(Padding padding) {
        var a = new Area.Simple(getData().getX(), getData().getY(), getData().getWidth() - scrollbar.getData()
            .getWidth(), getData().getHeight());
        this.contentArea = padding.apply(a);
    }

    public Area getContentArea() {
        return contentArea;
    }

    protected int getContainerHeight() {
        return content.getData().getHeight();
    }

    public Scrollbar getScrollbar() {
        return scrollbar;
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double value) {
        return TranslatableWidget.super.mouseScrolled(mouseX, mouseY, value)
            || scrollbar.mouseScrolled(mouseX, mouseY, value);
    }

    @Override
    public boolean isMouseOver(double mouseX, double mouseY) {
        return data.inBounds(mouseX, mouseY) || TranslatableWidget.super.isMouseOver(mouseX, mouseY);
    }

    @Override
    public WidgetData getData() {
        return data;
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int mouseButton) {
        return scrollbar.mouseClicked(mouseX, mouseY, mouseButton)
            || TranslatableWidget.super.mouseClicked(mouseX, mouseY, mouseButton);
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int mouseButton, double dragX, double dragY) {
        return scrollbar.mouseDragged(mouseX, mouseY, mouseButton, dragX, dragY)
            || TranslatableWidget.super.mouseDragged(mouseX, mouseY, mouseButton, dragX, dragY);
    }

    @Override
    public void render(PoseStack poseStack, int i, int j, float f) {
        render(new AlmostPoseStack(poseStack), i, j, f);
    }
}
