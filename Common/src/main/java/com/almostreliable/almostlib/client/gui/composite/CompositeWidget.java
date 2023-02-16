package com.almostreliable.almostlib.client.gui.composite;

import com.almostreliable.almostlib.client.gui.AlmostWidget;
import com.almostreliable.almostlib.client.gui.WidgetChangeListener;
import com.almostreliable.almostlib.client.gui.WidgetData;
import com.almostreliable.almostlib.client.gui.util.VanillaWidgetWrapper;
import com.almostreliable.almostlib.client.rendering.AlmostPoseStack;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.events.ContainerEventHandler;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarratableEntry;
import net.minecraft.client.gui.narration.NarrationElementOutput;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public abstract class CompositeWidget implements ContainerEventHandler, NarratableEntry, AlmostWidget<WidgetData>,
    WidgetChangeListener {

    private final WidgetData data;
    @Nullable private GuiEventListener focused;
    private boolean dragging;
    protected final List<GuiEventListener> eventListeners = new ArrayList<>();
    protected final List<AlmostWidget<?>> widgets = new ArrayList<>();

    private boolean requireRecalculation = true;

    public CompositeWidget(int x, int y, int width, int height) {
        this.data = WidgetData.of(x, y, width, height);
    }

    @Override
    public void render(AlmostPoseStack stack, int mouseX, int mouseY, float delta) {
        if (requireRecalculation) {
            calculateLayout();
            requireRecalculation = false;
        }
        renderWidgets(stack, mouseX, mouseY, delta);
    }

    public void renderWidgets(AlmostPoseStack stack, int mouseX, int mouseY, float delta) {
        for (AlmostWidget<?> widget : widgets) {
            if (widget.getData().isVisible()) {
                widget.render(stack, mouseX, mouseY, delta);
            }
        }
    }

    /**
     * Adds a widget. If the widget is a {@link GuiEventListener}, it will be added to the list of event listeners.
     * <p>
     * Adding a widget will cause the layout to be recalculated in the next render call.
     *
     * @param widget The widget to add
     * @param <T>    The data type of the widget. {@link WidgetData}
     * @return The widget that was added
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
     * Removes all widgets from this list.
     */
    public void clearWidgets() {
        widgets.forEach(w -> w.getData().setParent(null));
        widgets.clear();
        eventListeners.clear();
        markForRecalculation();
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
    public List<? extends GuiEventListener> children() {
        return eventListeners;
    }

    @Override
    public boolean isActive() {
        return getData().isActive();
    }

    @Override
    public NarrationPriority narrationPriority() {
        return NarrationPriority.NONE;
    }

    @Override
    public void updateNarration(NarrationElementOutput narrationElementOutput) {

    }

    @Override
    public WidgetData getData() {
        return data;
    }

    @Override
    public void onWidgetResize(AlmostWidget<?> widget) {
        markForRecalculation();
    }

    public void markForRecalculation() {
        requireRecalculation = true;
    }

    abstract protected void calculateLayout();
}
