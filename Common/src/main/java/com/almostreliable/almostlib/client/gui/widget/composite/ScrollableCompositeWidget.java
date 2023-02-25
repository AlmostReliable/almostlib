package com.almostreliable.almostlib.client.gui.widget.composite;

import com.almostreliable.almostlib.client.gui.Padding;
import com.almostreliable.almostlib.client.gui.Scrollbar;
import com.almostreliable.almostlib.client.gui.WidgetData;
import com.almostreliable.almostlib.client.gui.widget.layout.Layout;
import com.almostreliable.almostlib.client.gui.widget.layout.Layouts;
import com.almostreliable.almostlib.client.rendering.AlmostRendering;
import com.almostreliable.almostlib.util.Area;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.GuiComponent;

public class ScrollableCompositeWidget extends TranslatableCompositeWidget {

    private final WidgetData data;
    private Area contentArea;
    private final Scrollbar scrollbar;
    private boolean requiresUpdate = true;
    private int contentHeight;

    public ScrollableCompositeWidget(int x, int y, int width, int height) {
        this(x, y, width, height, Padding.of(0));
    }

    public ScrollableCompositeWidget(int x, int y, int width, int height, Padding contentPadding) {
        super(x, y, width, height);
        this.data = WidgetData.of(x, y, width, height);
        this.scrollbar = createScrollbar();
        setPadding(contentPadding);
        setLayout(Layouts.VERTICAL_STACK);
    }

    protected Scrollbar createScrollbar() {
        return new Scrollbar(getData().getRight() - 10, getData().getY(), 10, getData().getHeight());
    }

    @Override
    public void render(PoseStack poseStack, int mouseX, int mouseY, float delta) {
        if (AlmostRendering.isDebug()) {
            GuiComponent.fill(poseStack, getData().getX(), getData().getY(), getData().getRight(), getData().getBottom(), 0x80_00FF00);
        }

        GuiComponent.enableScissor(getContentArea().getX(), getContentArea().getY(), getContentArea().getRight(), getContentArea().getBottom());
        super.render(poseStack, mouseX, mouseY, delta);
        GuiComponent.disableScissor();

        if (scrollbar.getData().isVisible()) {
            if (requiresUpdate) {
                updateScrollbar();
                requiresUpdate = false;
            }
            scrollbar.updateHovered(mouseX, mouseY);
            renderScrollbar(poseStack, mouseX, mouseY, delta);
            if (AlmostRendering.isDebug()) {
                scrollbar.renderDebug(poseStack);
            }
        }
    }

    protected void renderScrollbar(PoseStack poseStack, int mouseX, int mouseY, float delta) {}

    @Override
    public double getTranslateX() {
        return 0;
    }

    @Override
    public double getTranslateY() {
        return -scrollbar.getValue();
    }

    @Override
    public double calcMouseXTranslation(double mouseX) {
        if (getContentArea().inHorizontalBounds(mouseX)) {
            return super.calcMouseXTranslation(mouseX);
        }
        return -Double.MAX_VALUE;
    }

    @Override
    public double calcMouseYTranslation(double mouseY) {
        if (getContentArea().inVerticalBounds(mouseY)) {
            return super.calcMouseYTranslation(mouseY);
        }
        return -Double.MAX_VALUE;
    }

    public void updateScrollbar() {
        scrollbar.setSliderHeight(Math.min(contentArea.getHeight() * contentArea.getHeight() / getContainerHeight(), data.getHeight()));
        scrollbar.setMaxScrollableHeight(getContainerHeight() - contentArea.getHeight());
    }

    @Override
    public void setPadding(Padding padding) {
        Padding newPadding = Padding.of(padding.top(), padding.right() + scrollbar.getData().getWidth(), padding.bottom(), padding.left());
        this.contentArea = newPadding.apply(getData());
        super.setPadding(newPadding);
    }

    public Area getContentArea() {
        return contentArea;
    }

    protected int getContainerHeight() {
        return contentHeight;
    }

    public Scrollbar getScrollbar() {
        return scrollbar;
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double value) {
        return super.mouseScrolled(mouseX, mouseY, value)
            || scrollbar.mouseScrolled(mouseX, mouseY, value);
    }

    @Override
    public boolean isMouseOver(double mouseX, double mouseY) {
        return data.inBounds(mouseX, mouseY) || super.isMouseOver(mouseX, mouseY);
    }

    @Override
    public WidgetData getData() {
        return data;
    }

    @Override
    protected void onLayoutCalculated(Layout.Result result) {
        contentHeight = result.getHeight();
        if (contentHeight <= 0) {
            contentHeight = contentArea.getHeight();
        }
        requiresUpdate = true;
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int mouseButton) {
        return scrollbar.mouseClicked(mouseX, mouseY, mouseButton)
            || super.mouseClicked(mouseX, mouseY, mouseButton);
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int mouseButton, double dragX, double dragY) {
        return scrollbar.mouseDragged(mouseX, mouseY, mouseButton, dragX, dragY)
            || super.mouseDragged(mouseX, mouseY, mouseButton, dragX, dragY);
    }
}
