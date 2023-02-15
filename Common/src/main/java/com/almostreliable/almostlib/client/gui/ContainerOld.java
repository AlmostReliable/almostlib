package com.almostreliable.almostlib.client.gui;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Widget;
import net.minecraft.client.gui.components.events.AbstractContainerEventHandler;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarratableEntry;
import net.minecraft.client.gui.narration.NarrationElementOutput;

import java.util.ArrayList;
import java.util.List;

// TODO to interface
public class ContainerOld extends AbstractContainerEventHandler implements Widget, NarratableEntry {

    private final List<GuiEventListener> children = new ArrayList<>();
    private final List<Widget> widgets = new ArrayList<>();
    private int height;
    private int width;
    private boolean requireRecalculation = true;

    public ContainerOld() {

    }

    public int getHeight() {
        if (requireRecalculation) {
            requireRecalculation = false;
            // TODO use custom widget interface with size
            this.height = widgets.stream()
                .filter(AbstractWidget.class::isInstance)
                .map(AbstractWidget.class::cast)
                .mapToInt(AbstractWidget::getHeight)
                .sum() + widgets.size() * 2;
        }
        return this.height;
    }

    public int getWidth() {
        return this.width;
    }

    @Override
    public void render(PoseStack poseStack, int mouseX, int mouseY, float delta) {
        widgets.forEach(w -> w.render(poseStack, mouseX, mouseY, delta));
    }

    public void addWidget(Widget widget) {
        widgets.add(widget);
        if (widget instanceof GuiEventListener listener) {
            children.add(listener);
        }
        requireRecalculation = true;
    }

    @Override
    public List<? extends GuiEventListener> children() {
        return children;
    }

    @Override
    public NarrationPriority narrationPriority() {
        return NarrationPriority.NONE;
    }

    @Override
    public void updateNarration(NarrationElementOutput narrationElementOutput) {

    }
}
