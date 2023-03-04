package com.almostreliable.almostlib.client.gui.widget.composite;

import com.almostreliable.almostlib.client.gui.Padding;
import com.almostreliable.almostlib.client.gui.Scrollbar;
import com.almostreliable.almostlib.client.gui.WidgetData;
import com.almostreliable.almostlib.client.gui.widget.layout.Layout;
import com.almostreliable.almostlib.client.gui.widget.layout.Layouts;
import com.almostreliable.almostlib.client.rendering.AlmostRendering;
import com.almostreliable.almostlib.util.Area;
import com.mojang.blaze3d.vertex.PoseStack;

import javax.annotation.Nullable;

/**
 * A scrollable implementation of {@link TranslatableCompositeWidget}.
 * <p>
 * Automatically handles layout, mouse translation and events for all its children.
 */
public class ScrollableCompositeWidget extends TranslatableCompositeWidget {

    private final WidgetData data;
    private final Scrollbar scrollbar;
    private Area contentArea;
    private boolean requiresUpdate = true;
    private int contentHeight;
    @Nullable private Area scissorArea;
    @Nullable private Padding scissorsPadding;

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

    @Override
    public void render(PoseStack poseStack, int mouseX, int mouseY, float delta) {
        if (AlmostRendering.isDebug()) {
            AlmostRendering.fill(poseStack, getData(), 0x8000_FF00);
            AlmostRendering.fill(poseStack, getScissorArea(), 0x8000_FFFF);
        }

        AlmostRendering.enableScissor(getScissorArea());
        super.render(poseStack, mouseX, mouseY, delta);
        AlmostRendering.endScissor();

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

    @Override
    protected void onLayoutCalculated(Layout.Result result) {
        contentHeight = result.getHeight();
        if (contentHeight <= 0) {
            contentHeight = contentArea.getHeight();
        }
        requiresUpdate = true;
    }

    @Override
    public void setPadding(Padding padding) {
        Padding paddingWithScrollbar = padding.addRight(scrollbar.getData().getWidth());
        super.setPadding(paddingWithScrollbar);
        updateContentArea();
    }

    @Override
    public WidgetData getData() {
        return data;
    }

    public void updateScrollbar() {
        scrollbar.setSliderHeight(Math.min(contentArea.getHeight() * contentArea.getHeight() / getContainerHeight(), data.getHeight()));
        scrollbar.setMaxScrollableHeight(getContainerHeight() - contentArea.getHeight());
    }

    protected Scrollbar createScrollbar() {
        return new Scrollbar(getData().getRight() - 10, getData().getY(), 10, getData().getHeight());
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
        if (getScissorArea().inHorizontalBounds(mouseX)) {
            return super.calcMouseXTranslation(mouseX);
        }
        return -Double.MAX_VALUE;
    }

    @Override
    public double calcMouseYTranslation(double mouseY) {
        if (getScissorArea().inVerticalBounds(mouseY)) {
            return super.calcMouseYTranslation(mouseY);
        }
        return -Double.MAX_VALUE;
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

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double value) {
        return super.mouseScrolled(mouseX, mouseY, value)
            || scrollbar.mouseScrolled(mouseX, mouseY, value);
    }

    @Override
    public boolean isMouseOver(double mouseX, double mouseY) {
        return data.inBounds(mouseX, mouseY) || super.isMouseOver(mouseX, mouseY);
    }

    public Area getScissorArea() {
        if (scissorArea == null) {
            return getContentArea();
        }

        return scissorArea;
    }

    public void setScissorPadding(@Nullable Padding padding) {
        this.scissorsPadding = padding;
        updateScissorArea();
    }

    public Area getContentArea() {
        return contentArea;
    }

    private void updateContentArea() {
        this.contentArea = getPadding().apply(getData());
    }

    private void updateScissorArea() {
        if (scissorsPadding != null) {
            this.scissorArea = scissorsPadding.apply(getData());
        }
    }

    protected int getContainerHeight() {
        return contentHeight;
    }

    public Scrollbar getScrollbar() {
        return scrollbar;
    }

    @Override
    public void resize(int width, int height) {
        super.resize(width, height);
        scrollbar.getData().setHeight(height); // TODO could not fit into all cases
        updateContentArea();
        updateScissorArea();
    }
}
