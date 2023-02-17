package com.almostreliable.almostlib.client.rendering;

import com.almostreliable.almostlib.util.Area;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Tuple;
import org.jetbrains.annotations.Nullable;

import java.util.*;

@Environment(EnvType.CLIENT)
public class AlmostRendering {

    @Nullable private static List<Component> tooltipLines;
    @Nullable private static Debug debug = new Debug(); // TODO make this configurable

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

    public static void fill(PoseStack poseStack, Area area, long color) {
        GuiComponent.fill(poseStack, area.getX(), area.getY(), area.getRight(), area.getBottom(), (int) color);
    }

    public static void border(PoseStack poseStack, Area area, int s, long color) {
        float alpha = (float) (color >> 24 & 0xFF) / 255.0f;
        float red = (float) (color >> 16 & 0xFF) / 255.0f;
        float green = (float) (color >> 8 & 0xFF) / 255.0f;
        float blue = (float) (color & 0xFF) / 255.0f;
        border(poseStack, area, s, red, green, blue, alpha);
    }

    private static void border(PoseStack poseStack, Area area, int s, float red, float green, float blue, float alpha) {
        var pose = poseStack.last().pose();

        int x = area.getX();
        int y = area.getY();
        int r = area.getRight();
        int b = area.getBottom();

        BufferBuilder buffer = Tesselator.getInstance().getBuilder();
        RenderSystem.enableBlend();
        RenderSystem.disableTexture();
        RenderSystem.defaultBlendFunc();

        RenderSystem.setShader(GameRenderer::getPositionColorShader);
        buffer.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR);

        // Vertices are built counter-clockwise
        // Top line
        buffer.vertex(pose, x - s, y - s, 0f).color(red, green, blue, alpha).endVertex();
        buffer.vertex(pose, x - s, y, 0f).color(red, green, blue, alpha).endVertex();
        buffer.vertex(pose, r + s, y, 0f).color(red, green, blue, alpha).endVertex();
        buffer.vertex(pose, r + s, y - s, 0f).color(red, green, blue, alpha).endVertex();

        // Right line
        buffer.vertex(pose, r, y, 0f).color(red, green, blue, alpha).endVertex();
        buffer.vertex(pose, r, b, 0f).color(red, green, blue, alpha).endVertex();
        buffer.vertex(pose, r + s, b, 0f).color(red, green, blue, alpha).endVertex();
        buffer.vertex(pose, r + s, y, 0f).color(red, green, blue, alpha).endVertex();

        // Bottom line
        buffer.vertex(pose, x - s, b, 0f).color(red, green, blue, alpha).endVertex();
        buffer.vertex(pose, x - s, b + s, 0f).color(red, green, blue, alpha).endVertex();
        buffer.vertex(pose, r + s, b + s, 0f).color(red, green, blue, alpha).endVertex();
        buffer.vertex(pose, r + s, b, 0f).color(red, green, blue, alpha).endVertex();

        // Left line
        buffer.vertex(pose, x, y, 0f).color(red, green, blue, alpha).endVertex();
        buffer.vertex(pose, x - s, y, 0f).color(red, green, blue, alpha).endVertex();
        buffer.vertex(pose, x - s, b, 0f).color(red, green, blue, alpha).endVertex();
        buffer.vertex(pose, x, b, 0f).color(red, green, blue, alpha).endVertex();

        BufferUploader.drawWithShader(buffer.end());
        RenderSystem.enableTexture();
        RenderSystem.disableBlend();
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

    public static void renderAndClearDebug(PoseStack poseStack) {
        if (debug != null) {
            debug.renderAndClear(poseStack);
        }
    }

    private static class Debug {

        private final Map<String, List<Tuple<String, String>>> data = new LinkedHashMap<>();

        public Debug() {
            data.put("", new ArrayList<>());
        }

        public void write(String key, Object value) {
            write("", key, value);
        }

        public void write(String category, String key, Object value) {
            data.computeIfAbsent(category, k -> new ArrayList<>()).add(new Tuple<>(key, value.toString()));
        }

        public void renderAndClear(PoseStack ps) {
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

            data.clear();
        }
    }
}
