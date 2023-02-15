package com.almostreliable.almostlib.client.rendering;

import com.google.common.collect.Queues;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Matrix4f;
import com.mojang.math.Quaternion;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Tuple;
import net.minecraft.world.phys.Vec2;

import javax.annotation.Nullable;
import java.util.*;
import java.util.function.Consumer;

@Environment(EnvType.CLIENT)
public class RenderContext extends PoseStack implements Translatable<RenderContext>, Scalable<RenderContext>, Rotatable<RenderContext> {

    private final PoseStack stack;
    private final int mouseX;
    private final int mouseY;
    private final float delta;
    @Nullable TooltipData tooltipData;
    @Nullable Debug debug;

    private final Deque<MouseHistory> mouseHistory = Queues.newArrayDeque();

    public RenderContext(PoseStack stack, int mouseX, int mouseY, float delta) {
        this.stack = stack;
        this.mouseX = mouseX;
        this.mouseY = mouseY;
        this.delta = delta;
        mouseHistory.add(new MouseHistory());
        debug = Debug.EMPTY; // new DebugImpl(); TODO: Make this configurable
    }

//    public static RenderContext createAndRun(PoseStack ps, int mouseX, int mouseY, float delta, Renderable renderable) {
//        return createAndRun(ps, mouseX, mouseY, delta, renderable, Minecraft.getInstance().screen);
//    }
//
//    public static RenderContext createAndRun(PoseStack stack, int mouseX, int mouseY, float delta, Renderable renderable, @Nullable Screen caller) {
//        RenderContext ctx = new RenderContext(stack, mouseX, mouseY, delta);
//        renderable.render(ctx);
//        ctx.renderDebug();
//        if (caller != null) {
//            ctx.releaseTooltip(caller);
//        }
//        return ctx;
//    }

    public void runPose(Runnable r) {
        pushPose();
        r.run();
        popPose();
    }

    public void runPose(Consumer<RenderContext> r) {
        pushPose();
        r.accept(this);
        popPose();
    }

    //region Default pose stack overrides
    @Override
    public void pushPose() {
        stack.pushPose();
        mouseHistory.add(lastMouseHistory().copy());
    }

    @Override
    public void popPose() {
        stack.popPose();
        mouseHistory.removeLast();
    }

    @Override
    public void translate(double x, double y, double z) {
        translateAll(x, y, z);
    }

    @Override
    public void scale(float x, float y, float z) {
        scaleAll(x, y, z);
    }

    @Override
    public void mulPose(Quaternion quaternion) {
        rotate(quaternion);
    }

    @Override
    public Pose last() {
        return stack.last();
    }

    @Override
    public boolean clear() {
        return stack.clear();
    }

    @Override
    public void setIdentity() {
        stack.setIdentity();
        lastMouseHistory().reset();
    }

    @Override
    public void mulPoseMatrix(Matrix4f matrix4f) {
        stack.mulPoseMatrix(matrix4f);
    }
    //endregion

    @Override
    public RenderContext rotate(Quaternion rotation) {
        stack.mulPose(rotation);
        mouseHistory.getLast().mul(rotation);
        return this;
    }

    @Override
    public RenderContext translateAll(double x, double y, double z) {
        stack.translate(x, y, z);
        lastMouseHistory().translate(x, y);
        return this;
    }

    @Override
    public RenderContext scaleAll(float x, float y, float z) {
        stack.scale(x, y, z);
        lastMouseHistory().scale(x, y);
        return this;
    }

    public Vec2 getRelativeMouse() {
        MouseHistory last = lastMouseHistory();
        float mx = (float) (mouseX / last.scaleX + last.x);
        float my = (float) (mouseY / last.scaleY + last.y);

        // taken from Vector3f#transform(Quaternion)
        Quaternion actualRotation = last.rotation.copy();
        actualRotation.conj();
        var resultRotation = actualRotation.copy();
        resultRotation.mul(new Quaternion(mx, my, 1.0f, 0.0f));
        var conjRotation = actualRotation.copy();
        conjRotation.conj();
        resultRotation.mul(conjRotation);

        return new Vec2(resultRotation.i(), resultRotation.j());
    }

    private MouseHistory lastMouseHistory() {
        return mouseHistory.getLast();
    }

    public PoseStack getStack() {
        return stack;
    }

    public void releaseTooltip(Screen screen) {
        if (tooltipData != null) {
            var m = getRelativeMouse();
            screen.renderTooltip(stack, tooltipData.lines, Optional.empty(), (int) m.x, (int) m.y);
        }
    }

    public float getDelta() {
        return delta;
    }

    public void queueTooltip(Component component) {
        queueTooltip(List.of(component));
    }

    public void queueTooltip(List<Component> lines) {
        tooltipData = new TooltipData(lines);
    }

    public void write(int x, int y, String text, long color) {
        GuiComponent.drawString(stack, Minecraft.getInstance().font, text, x, y, (int) color);
    }

    public void write(int x, int y, String text, ChatFormatting color) {
        //noinspection ConstantConditions
        write(x, y, text, color.getColor());
    }

    public void write(int x, int y, Component component) {

    }

    public void debug(String key, Object value) {
        if (debug != null) {
            debug.write(key, value);
        }
    }

    public void debug(String category, String key, Object value) {
        if (debug != null) {
            debug.write(category, key, value);
        }
    }

    public void renderDebug() {
        if (debug != null) {
            debug.render();
        }
    }

    private record TooltipData(List<Component> lines) {

    }

    private static class MouseHistory {

        private double x;
        private double y;
        private float scaleX;
        private float scaleY;
        private Quaternion rotation;

        private MouseHistory() {
            reset();

        }

        public void reset() {
            this.x = 0;
            this.y = 0;
            this.scaleX = 1;
            this.scaleY = 1;
            this.rotation = Quaternion.ONE.copy();
        }

        public void translate(double x, double y) {
            this.x -= x;
            this.y -= y;
        }

        public void scale(float sx, float sy) {
            this.scaleX *= sx;
            this.scaleY *= sy;
        }

        public void mul(Quaternion q) {
            rotation.mul(q);
        }

        public MouseHistory copy() {
            var mh = new MouseHistory();
            mh.x = x;
            mh.y = y;
            mh.scaleX = scaleX;
            mh.scaleY = scaleY;
            mh.rotation = rotation.copy();
            return mh;
        }
    }

    private interface Debug {

        Debug EMPTY = new Debug() {
        };

        default void write(String key, Object value) {
            write("", key, value);
        }

        default void write(String category, String key, Object value) {

        }

        default void render() {}


    }

    private class DebugImpl implements Debug {

        private final Map<String, List<Tuple<String, String>>> data = new LinkedHashMap<>();

        public DebugImpl() {
            data.put("", new ArrayList<>());
        }

        @Override
        public void write(String category, String key, Object value) {
            data.computeIfAbsent(category, k -> new ArrayList<>()).add(new Tuple<>(key, value.toString()));
        }

        @Override
        public void render() {
            PoseStack ps = RenderContext.this.stack;
            var font = Minecraft.getInstance().font;
            int lineIndex = 0;
            ps.pushPose();
            ps.scale(0.7f, 0.7f, 1);
            for (var e : data.entrySet()) {
                if (!e.getKey().isEmpty()) {
                    GuiComponent.drawString(ps, font, "[ " + e.getKey() + " ]", 2, 2 + lineIndex * font.lineHeight, 0xFFFFFF);
                }

                var longestString = e.getValue().stream().map(Tuple::getA).max(Comparator.comparingInt(String::length)).orElse("");
                int width = font.width(longestString);

                for (var kv : e.getValue()) {
                    lineIndex++;
                    GuiComponent.drawString(ps, font, kv.getA(), 2, 2 + lineIndex * font.lineHeight, 0xFFFFFF);
                    GuiComponent.drawString(ps, font, " = " + kv.getB(), 2 + width, 2 + lineIndex * font.lineHeight, 0xFFFFFF);
                }

                lineIndex += 3;
            }
            ps.popPose();
        }
    }
}
