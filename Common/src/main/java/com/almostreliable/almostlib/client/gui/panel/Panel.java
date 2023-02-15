package com.almostreliable.almostlib.client.gui.panel;

import com.almostreliable.almostlib.client.gui.AlmostWidget;
import com.almostreliable.almostlib.client.gui.WidgetData;
import com.mojang.blaze3d.vertex.PoseStack;

import java.util.ArrayList;
import java.util.Collection;

public class Panel implements ContainerWidget<WidgetData> {

    private final WidgetData data;

    private final Collection<AlmostWidget<?>> children;

    public Panel() {
        this.data = new WidgetData(110, 110, 40, 40);
        children = new ArrayList<>();
    }

//    @Override
//    public void render(RenderContext ctx) {
//        var mt = ctx.getRelativeMouse();
//
//        ctx.debug("Relative Mouse X", mt.x);
//        ctx.debug("Relative Mouse Y", mt.y);
//
//        boolean b = inBounds((int) mt.x, (int) mt.y);
//        GuiComponent.fill(ctx.getStack(), data.getX(), data.getY(), data.getX() + data.getWidth(), data.getY() + data.getHeight(), b
//            ? 0x80FF0000
//            : 0x800000FF);
//
//        for (AlmostWidget<?> child : children) {
//            if (child.getData().isVisible()) {
//                child.render(ctx);
//            }
//        }

    @Override
    public void render(PoseStack stack, int offsetX, int offsetY, int mouseX, int mouseY, float delta) {

    }
    //    }

    @Override
    public WidgetData getData() {
        return data;
    }

    @Override
    public Collection<AlmostWidget<?>> getChildren() {
        return children;
    }

    @Override
    public void addChild(AlmostWidget<?> child) {
        children.add(child);
    }
}
