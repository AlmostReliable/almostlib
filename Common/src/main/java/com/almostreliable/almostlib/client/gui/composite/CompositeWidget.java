package com.almostreliable.almostlib.client.gui.composite;

import com.almostreliable.almostlib.client.gui.AlmostWidget;
import com.almostreliable.almostlib.client.gui.WidgetChangeListener;
import com.almostreliable.almostlib.client.gui.WidgetData;
import com.almostreliable.almostlib.client.rendering.AlmostPoseStack;
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
        for (AlmostWidget<?> widget : widgets) {
            if (widget.getData().isVisible()) {
                widget.render(stack, mouseX, mouseY, delta);
            }
        }
    }

    public void addWidget(AlmostWidget<?> widget) {
        widgets.add(widget);
        widget.getData().setParent(this);
        if (widget instanceof GuiEventListener listener) {
            eventListeners.add(listener);
        }
    }

    public void removeWidget(AlmostWidget<?> widget) {
        widgets.remove(widget);
        widget.getData().setParent(null);
        if (widget instanceof GuiEventListener listener) {
            eventListeners.remove(listener);
        }
        markForRecalculation();
    }

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
