package com.almostreliable.almostlib.client.rendering;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Matrix4f;
import com.mojang.math.Quaternion;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Tuple;

import javax.annotation.Nullable;
import java.util.*;
import java.util.function.Consumer;

public class AlmostPoseStack extends PoseStack implements Translatable<AlmostPoseStack>, Scalable<AlmostPoseStack>,
    Rotatable<AlmostPoseStack> {

    private final PoseStack stack;
    @Nullable private List<Component> tooltipLines;
    @Nullable private Debug debug;

    public AlmostPoseStack(PoseStack stack) {
        this.stack = stack;
        this.debug = new DebugImpl(); // TODO make this configurable
    }

    public void runPose(Runnable r) {
        pushPose();
        r.run();
        popPose();
    }

    public void runPose(Consumer<AlmostPoseStack> r) {
        pushPose();
        r.accept(this);
        popPose();
    }

    @Override
    public AlmostPoseStack rotate(Quaternion rotation) {
        stack.mulPose(rotation);
        return this;
    }

    @Override
    public AlmostPoseStack scaleAll(float x, float y, float z) {
        stack.scale(x, y, z);
        return this;
    }

    @Override
    public AlmostPoseStack translateAll(double x, double y, double z) {
        stack.translate(x, y, z);
        return this;
    }

    public void queueTooltip(Component component) {
        queueTooltip(List.of(component));
    }

    public void queueTooltip(List<Component> lines) {
        tooltipLines = lines;
    }

    public void releaseTooltip(Screen screen, double mouseX, double mouseY) {
        if (tooltipLines != null) {
            screen.renderTooltip(stack, tooltipLines, Optional.empty(), (int) mouseX, (int) mouseY);
        }
    }

    //region PoseStack delegates
    @Override
    public void translate(double d, double e, double f) {
        stack.translate(d, e, f);
    }

    @Override
    public void scale(float f, float g, float h) {
        stack.scale(f, g, h);
    }

    @Override
    public void mulPose(Quaternion quaternion) {
        stack.mulPose(quaternion);
    }

    @Override
    public void pushPose() {
        stack.pushPose();
    }

    @Override
    public void popPose() {
        stack.popPose();
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
    }

    @Override
    public void mulPoseMatrix(Matrix4f matrix4f) {
        stack.mulPoseMatrix(matrix4f);
    }
    //endregion

    //region Debug helpers
    public boolean isDebug() {
        return debug != null;
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
            debug.render(stack);
        }
    }

    private interface Debug {


        default void write(String key, Object value) {
            write("", key, value);
        }

        default void write(String category, String key, Object value) {

        }

        default void render(PoseStack ps) {}
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
        public void render(PoseStack ps) {
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
    //endregion
}
