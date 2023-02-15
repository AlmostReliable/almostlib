package com.almostreliable.almostlib.client.gui;

import net.minecraft.client.gui.components.events.ContainerEventHandler;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarratableEntry;

import javax.annotation.Nullable;

public interface CompositeWidget extends ContainerEventHandler, NarratableEntry, WidgetDataProvider<CompositeWidget.Data> {

    @Override
    default boolean isDragging() {
        return getData().isDragging();
    }

    @Override
    default void setDragging(boolean bl) {
        getData().setDragging(bl);
    }

    @Override
    @Nullable
    default GuiEventListener getFocused() {
        return getData().getFocused();
    }

    @Override
    default void setFocused(@Nullable GuiEventListener guiEventListener) {
        getData().setFocused(guiEventListener);
    }


    class Data extends WidgetData {

        @Nullable private GuiEventListener focused;
        private boolean dragging;

        public Data(int x, int y, int width, int height) {
            super(x, y, width, height);
        }

        public void setFocused(@Nullable GuiEventListener guiEventListener) {
            this.focused = guiEventListener;
        }

        @Nullable
        public GuiEventListener getFocused() {
            return focused;
        }

        public void setDragging(boolean dragging) {
            this.dragging = dragging;
        }

        public boolean isDragging() {
            return dragging;
        }
    }
}
