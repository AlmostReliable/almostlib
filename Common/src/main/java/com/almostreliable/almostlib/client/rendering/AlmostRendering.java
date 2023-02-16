package com.almostreliable.almostlib.client.rendering;

import com.mojang.blaze3d.vertex.PoseStack;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Tuple;
import org.jetbrains.annotations.Nullable;

import java.util.*;

@Environment(EnvType.CLIENT)
public class AlmostRendering {

    @Nullable private static List<Component> tooltipLines;
    @Nullable private static Debug debug = new DebugImpl(); // TODO make this configurable

    public static boolean isDebug() {
        return true;
    }

    public static void writeTooltip(Component... component) {
        writeTooltip(List.of(component));
    }

    public static void writeTooltip(List<Component> lines) {
        tooltipLines = lines;
    }

    public static void releaseTooltip(Screen screen, PoseStack poseStack, double mouseX, double mouseY) {
        if (tooltipLines != null) {
            screen.renderTooltip(poseStack, tooltipLines, Optional.empty(), (int) mouseX, (int) mouseY);
        }
    }

    public static void debug(String key, Object value) {
        if (debug != null) {
            debug.write(key, value);
        }
    }

    public static void debug(String category, String key, Object value) {
        if (debug != null) {
            debug.write(category, key, value);
        }
    }

    public static void renderDebug(PoseStack poseStack) {
        if (debug != null) {
            debug.render(poseStack);
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

    private static class DebugImpl implements Debug {

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
            data.clear(); // TODO outsource clear?
        }
    }
}
