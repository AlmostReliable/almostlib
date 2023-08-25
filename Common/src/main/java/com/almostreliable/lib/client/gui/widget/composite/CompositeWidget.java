package com.almostreliable.lib.client.gui.widget.composite;

import com.almostreliable.lib.client.gui.Padding;
import com.almostreliable.lib.client.gui.WidgetChangeListener;
import com.almostreliable.lib.client.gui.WidgetData;
import com.almostreliable.lib.client.gui.widget.AlmostWidget;
import com.almostreliable.lib.client.gui.widget.VanillaWidgetWrapper;
import com.almostreliable.lib.client.gui.widget.layout.Layout;
import com.almostreliable.lib.client.gui.widget.layout.Layouts;
import com.almostreliable.lib.client.rendering.AlmostRendering;
import com.almostreliable.lib.util.Area;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.events.ContainerEventHandler;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarratableEntry;
import net.minecraft.client.gui.narration.NarrationElementOutput;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * A widget containing other widgets.
 * <p>
 * Automatically handles layout and events for all its children.
 */
public class CompositeWidget implements ContainerEventHandler, NarratableEntry, AlmostWidget<WidgetData>, WidgetChangeListener {

    private final WidgetData data;
    @Nullable private GuiEventListener focused;
    private boolean dragging;
    protected final List<GuiEventListener> eventListeners = new ArrayList<>();
    protected final List<AlmostWidget<?>> widgets = new ArrayList<>();
    protected boolean requiresRecalculation = true;
    protected Layout layout = Layouts.NOTHING;
    private boolean fullWidthWidgets;
    private int horizontalSpacing;
    private int verticalSpacing;
    private Padding padding = Padding.ZERO;

    public CompositeWidget(int x, int y, int width, int height) {
        this.data = WidgetData.of(x, y, width, height);
    }

    public CompositeWidget(Area area) {
        this.data = WidgetData.of(area);
    }

    @Override
    public void render(PoseStack stack, int mouseX, int mouseY, float delta) {
        if (requiresRecalculation) {
            calculateLayout();
            requiresRecalculation = false;
        }
        AlmostWidget.super.render(stack, mouseX, mouseY, delta);
        if (AlmostRendering.isDebug()) {
            AlmostRendering.fill(stack, getData(), 0x80FF_0000);
        }
        renderWidgets(stack, mouseX, mouseY, delta);
    }

    public void renderWidgets(PoseStack stack, int mouseX, int mouseY, float delta) {
        for (AlmostWidget<?> widget : widgets) {
            if (widget.getData().isVisible()) {
                widget.render(stack, mouseX, mouseY, delta);
            }
        }
    }

    /**
     * Adds a widget. If the widget is a {@link GuiEventListener}, it will be added to the list of event listeners.
     * <p>
     * Adding a widget will cause the layout to be recalculated within the next render call.
     *
     * @param widget The widget to add.
     * @param <T>    The data type of the widget implementing {@link WidgetData}.
     * @return The widget that was added.
     */
    public <T extends WidgetData> AlmostWidget<T> addWidget(AlmostWidget<T> widget) {
        widgets.add(widget);
        widget.getData().setParent(this);
        if (widget instanceof GuiEventListener listener) {
            eventListeners.add(listener);
        }
        markForRecalculation();
        return widget;
    }

    public AlmostWidget<WidgetData> addVanillaWidget(AbstractWidget vanillaWidget) {
        return addWidget(VanillaWidgetWrapper.wrap(vanillaWidget));
    }

    /**
     * Removes the first occurrence of the specified widget from this list, if it is present. Will use {@link Object#equals(Object)} to compare.
     * If the widget is a {@link GuiEventListener}, it will be removed from the list of event listeners.
     * <p>
     * Removing a widget will cause the layout to be recalculated in the next render call.
     *
     * @param widget The widget to remove
     * @return true if the widget was removed, false otherwise
     */
    public boolean removeWidget(AlmostWidget<?> widget) {
        boolean removed = widgets.remove(widget);
        widget.getData().setParent(null);
        if (widget instanceof GuiEventListener listener) {
            eventListeners.remove(listener);
        }
        if (removed) {
            markForRecalculation();
        }
        return removed;
    }

    public boolean removeVanillaWidget(AbstractWidget vanillaWidget) {
        return removeWidget(VanillaWidgetWrapper.wrap(vanillaWidget));
    }

    /**
     * Removes all widgets from this composite.
     */
    public void clearWidgets() {
        widgets.forEach(w -> w.getData().setParent(null));
        widgets.clear();
        eventListeners.clear();
        markForRecalculation();
    }

    @Override
    public List<? extends GuiEventListener> children() {
        return eventListeners;
    }

    @Override
    public boolean isDragging() {
        return dragging;
    }

    @Override
    public void setDragging(boolean dragging) {
        this.dragging = dragging;
    }

    @Override
    @Nullable
    public GuiEventListener getFocused() {
        return focused;
    }

    @Override
    public void setFocused(@Nullable GuiEventListener listener) {
        focused = listener;
    }

    @Override
    public NarrationPriority narrationPriority() {
        return NarrationPriority.NONE;
    }

    @Override
    public boolean isActive() {
        return getData().isActive();
    }

    @Override
    public void updateNarration(NarrationElementOutput narrationElementOutput) {}

    @Override
    public void onWidgetResize(AlmostWidget<?> widget) {
        markForRecalculation();
    }

    public void markForRecalculation() {
        requiresRecalculation = true;
    }

    protected void calculateLayout() {
        Layout.Result result = getLayout().calculate(this);
        if (result != null) {
            onLayoutCalculated(result);
        }
    }

    protected void onLayoutCalculated(Layout.Result result) {}

    public Collection<AlmostWidget<?>> getWidgets() {
        return widgets;
    }

    public Layout getLayout() {
        return layout;
    }

    public void setLayout(Layout layout) {
        this.layout = layout;
    }

    /**
     * Whether the composite renders children at full width.
     * <p>
     * When enabled, each child will have the same width as the composite.
     *
     * @return True when full width rendering is enabled, false otherwise.
     */
    public boolean isFullWidthWidgets() {
        return fullWidthWidgets;
    }

    public void setFullWidthWidgets(boolean fullWidth) {
        this.fullWidthWidgets = fullWidth;
        markForRecalculation();
    }

    public int getHorizontalSpacing() {
        return horizontalSpacing;
    }

    public void setHorizontalSpacing(int spacing) {
        this.horizontalSpacing = spacing;
        markForRecalculation();
    }

    public int getVerticalSpacing() {
        return verticalSpacing;
    }

    public void setVerticalSpacing(int spacing) {
        this.verticalSpacing = spacing;
        markForRecalculation();
    }

    public Padding getPadding() {
        return padding;
    }

    public void setPadding(Padding padding) {
        this.padding = padding;
        markForRecalculation();
    }

    @Override
    public WidgetData getData() {
        return data;
    }
}
