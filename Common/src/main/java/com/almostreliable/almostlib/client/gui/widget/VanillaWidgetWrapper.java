package com.almostreliable.almostlib.client.gui.widget;

import com.almostreliable.almostlib.client.gui.WidgetChangeListener;
import com.almostreliable.almostlib.client.gui.WidgetData;
import com.almostreliable.almostlib.mixin.AbstractWidgetAccessor;
import com.almostreliable.almostlib.util.Area;
import com.mojang.blaze3d.vertex.PoseStack;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.events.GuiEventListener;
import org.jetbrains.annotations.Nullable;

@Environment(EnvType.CLIENT)
public final class VanillaWidgetWrapper implements AlmostWidget<WidgetData>, GuiEventListener {

    private final AbstractWidget widget;
    private final Data data;

    public static VanillaWidgetWrapper wrap(AbstractWidget widget) {
        return new VanillaWidgetWrapper(widget);
    }

    private VanillaWidgetWrapper(AbstractWidget widget) {
        this.widget = widget;
        this.data = new Data();
    }

    @Override
    public WidgetData getData() {
        return data;
    }

    @Override
    public void render(PoseStack stack, int mouseX, int mouseY, float delta) {
        widget.render(stack, mouseX, mouseY, delta);
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof AbstractWidget && widget.equals(obj);
    }

    public AbstractWidget getWidget() {
        return widget;
    }

    //region GuiEventListener delegate methods
    @Override
    public void mouseMoved(double d, double e) {
        widget.mouseMoved(d, e);
    }

    @Override
    public boolean mouseClicked(double d, double e, int i) {
        return widget.mouseClicked(d, e, i);
    }

    @Override
    public boolean mouseReleased(double d, double e, int i) {
        return widget.mouseReleased(d, e, i);
    }

    @Override
    public boolean mouseDragged(double d, double e, int i, double f, double g) {
        return widget.mouseDragged(d, e, i, f, g);
    }

    @Override
    public boolean mouseScrolled(double d, double e, double f) {
        return widget.mouseScrolled(d, e, f);
    }

    @Override
    public boolean keyPressed(int i, int j, int k) {
        return widget.keyPressed(i, j, k);
    }

    @Override
    public boolean keyReleased(int i, int j, int k) {
        return widget.keyReleased(i, j, k);
    }

    @Override
    public boolean charTyped(char c, int i) {
        return widget.charTyped(c, i);
    }

    @Override
    public boolean changeFocus(boolean bl) {
        return widget.changeFocus(bl);
    }

    @Override
    public boolean isMouseOver(double d, double e) {
        return widget.isMouseOver(d, e);
    }
    //endregion

    public final class Data implements WidgetData {

        @Nullable private WidgetChangeListener parent;
        private final Area originArea;

        private Data() {
            originArea = Area.of(getX(), getY(), getWidth(), getHeight());
        }

        private AbstractWidgetAccessor asAccessor() {
            return (AbstractWidgetAccessor) VanillaWidgetWrapper.this.widget;
        }

        @Override
        public Area getOriginArea() {
            return originArea;
        }

        @Override
        public void setActive(boolean active) {
            VanillaWidgetWrapper.this.widget.active = active;
        }

        @Override
        public boolean isActive() {
            return VanillaWidgetWrapper.this.widget.active;
        }

        @Override
        public void setVisible(boolean visible) {
            VanillaWidgetWrapper.this.widget.visible = visible;
        }

        @Override
        public boolean isVisible() {
            return VanillaWidgetWrapper.this.widget.visible;
        }

        @Override
        public void setAlpha(float alpha) {
            VanillaWidgetWrapper.this.widget.setAlpha(alpha);
        }

        @Override
        public float getAlpha() {
            return asAccessor().getAlpha();
        }

        @Override
        public void setHovered(boolean hovered) {
            // no-op - this is handled by the vanilla widget
        }

        @Override
        public boolean isHovered() {
            return asAccessor().isHovered();
        }

        @Override
        public void setParent(@Nullable WidgetChangeListener parent) {
            this.parent = parent;
        }

        @Nullable
        @Override
        public WidgetChangeListener getParent() {
            return parent;
        }

        @Override
        public int getX() {
            return VanillaWidgetWrapper.this.widget.x;
        }

        @Override
        public int getY() {
            return VanillaWidgetWrapper.this.widget.y;
        }

        @Override
        public int getWidth() {
            return VanillaWidgetWrapper.this.widget.getWidth();
        }

        @Override
        public int getHeight() {
            return VanillaWidgetWrapper.this.widget.getHeight();
        }

        @Override
        public void setX(int x) {
            VanillaWidgetWrapper.this.widget.x = x;
        }

        @Override
        public void setY(int y) {
            VanillaWidgetWrapper.this.widget.y = y;
        }

        @Override
        public void setWidth(int width) {
            VanillaWidgetWrapper.this.widget.setWidth(width);
        }

        @Override
        public void setHeight(int height) {
            asAccessor().setHeight(height);
        }
    }
}
