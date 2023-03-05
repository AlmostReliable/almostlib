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
public final class AlmostRendering {

    @Nullable private static List<Component> TOOLTIP_LINES = null;
    @Nullable private static Debug DEBUG = null; // TODO make this configurable
    private static boolean IS_DEBUG = false;
    private static float renderScale = 1.0f;

    private AlmostRendering() {}

    public static boolean isDebug() {
        return IS_DEBUG;
    }

    public static void toggleDebug() {
        IS_DEBUG = !IS_DEBUG;
        DEBUG = IS_DEBUG ? new Debug() : null;
    }

    public static void setRenderScale(float scale) {
        renderScale = scale;
    }

    public static float getRenderScale() {
        return renderScale;
    }

    public static void resetRenderScale() {
        renderScale = 1.0f;
    }

    public static void writeTooltip(Component... component) {
        writeTooltip(List.of(component));
    }

    public static void writeTooltip(List<Component> lines) {
        TOOLTIP_LINES = lines;
    }

    public static void releaseTooltip(Screen screen, PoseStack poseStack, double mouseX, double mouseY) {
        if (TOOLTIP_LINES != null) {
            screen.renderTooltip(poseStack, TOOLTIP_LINES, Optional.empty(), (int) mouseX, (int) mouseY);
        }
    }

    public static void fill(PoseStack poseStack, Area area, long color) {
        fill(poseStack, area.getX(), area.getY(), area.getRight(), area.getBottom(), 0, color);
    }

    public static void fill(PoseStack poseStack, Area area, int depth, long color) {
        fill(poseStack, area.getX(), area.getY(), area.getRight(), area.getBottom(), depth, color);
    }

    public static void fill(PoseStack poseStack, int x1, int y1, int x2, int y2, long color) {
        fill(poseStack, x1, y1, x2, y2, 0, color);
    }

    public static void fill(PoseStack poseStack, int x1, int y1, int x2, int y2, int depth, long color) {
        var matrix4f = poseStack.last().pose();
        float alpha = (color >> 24 & 255) / 255.0F;
        float red = (color >> 16 & 255) / 255.0F;
        float green = (color >> 8 & 255) / 255.0F;
        float blue = (color & 255) / 255.0F;
        BufferBuilder bufferBuilder = Tesselator.getInstance().getBuilder();
        RenderSystem.enableBlend();
        RenderSystem.disableTexture();
        RenderSystem.defaultBlendFunc();
        RenderSystem.setShader(GameRenderer::getPositionColorShader);
        bufferBuilder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR);
        bufferBuilder.vertex(matrix4f, x1, y2, depth).color(red, green, blue, alpha).endVertex();
        bufferBuilder.vertex(matrix4f, x2, y2, depth).color(red, green, blue, alpha).endVertex();
        bufferBuilder.vertex(matrix4f, x2, y1, depth).color(red, green, blue, alpha).endVertex();
        bufferBuilder.vertex(matrix4f, x1, y1, depth).color(red, green, blue, alpha).endVertex();
        BufferUploader.drawWithShader(bufferBuilder.end());
        RenderSystem.enableTexture();
        RenderSystem.disableBlend();
    }

    public static void border(PoseStack poseStack, Area area, long color, int strength) {
        float alpha = (color >> 24 & 0xFF) / 255.0f;
        float red = (color >> 16 & 0xFF) / 255.0f;
        float green = (color >> 8 & 0xFF) / 255.0f;
        float blue = (color & 0xFF) / 255.0f;
        border(poseStack, area, strength, red, green, blue, alpha);
    }

    public static void debug(String key, Object value) {
        if (DEBUG != null) {
            DEBUG.write(key, value);
        }
    }

    public static void debug(String category, String key, Object value) {
        if (DEBUG != null) {
            DEBUG.write(category, key, value);
        }
    }

    public static void renderAndClearDebug(PoseStack poseStack) {
        if (DEBUG != null) {
            DEBUG.renderAndClear(poseStack);
        }
    }

    private static void border(PoseStack poseStack, Area area, int strength, float red, float green, float blue, float alpha) {
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
        buffer.vertex(pose, x - strength, y - strength, 0f).color(red, green, blue, alpha).endVertex();
        buffer.vertex(pose, x - strength, y, 0f).color(red, green, blue, alpha).endVertex();
        buffer.vertex(pose, r + strength, y, 0f).color(red, green, blue, alpha).endVertex();
        buffer.vertex(pose, r + strength, y - strength, 0f).color(red, green, blue, alpha).endVertex();

        // Right line
        buffer.vertex(pose, r, y, 0f).color(red, green, blue, alpha).endVertex();
        buffer.vertex(pose, r, b, 0f).color(red, green, blue, alpha).endVertex();
        buffer.vertex(pose, r + strength, b, 0f).color(red, green, blue, alpha).endVertex();
        buffer.vertex(pose, r + strength, y, 0f).color(red, green, blue, alpha).endVertex();

        // Bottom line
        buffer.vertex(pose, x - strength, b, 0f).color(red, green, blue, alpha).endVertex();
        buffer.vertex(pose, x - strength, b + strength, 0f).color(red, green, blue, alpha).endVertex();
        buffer.vertex(pose, r + strength, b + strength, 0f).color(red, green, blue, alpha).endVertex();
        buffer.vertex(pose, r + strength, b, 0f).color(red, green, blue, alpha).endVertex();

        // Left line
        buffer.vertex(pose, x, y, 0f).color(red, green, blue, alpha).endVertex();
        buffer.vertex(pose, x - strength, y, 0f).color(red, green, blue, alpha).endVertex();
        buffer.vertex(pose, x - strength, b, 0f).color(red, green, blue, alpha).endVertex();
        buffer.vertex(pose, x, b, 0f).color(red, green, blue, alpha).endVertex();

        BufferUploader.drawWithShader(buffer.end());
        RenderSystem.enableTexture();
        RenderSystem.disableBlend();
    }

    private static final class Debug {

        private final Map<String, List<Tuple<String, String>>> data = new LinkedHashMap<>();

        private Debug() {
            data.put("", new ArrayList<>());
        }

        public void write(String key, Object value) {
            write("", key, value);
        }

        public void write(String category, String key, Object value) {
            data.computeIfAbsent(category, k -> new ArrayList<>()).add(new Tuple<>(key, value.toString()));
        }

        public void renderAndClear(PoseStack ps) {
            boolean renderBackground = true; // TODO config

            var font = Minecraft.getInstance().font;
            int diffWidth = font.width(" = ") + 4;
            int lineIndex = 0;
            ps.pushPose();
            ps.scale(0.7f, 0.7f, 1);
            for (var e : data.entrySet()) {
                var category = e.getKey();
                var valuePair = e.getValue();

                if (!category.isEmpty()) {
                    GuiComponent.drawString(ps, font, "[ " + category + " ]", 2, 2 + lineIndex * font.lineHeight, 0xFFFFFF);
                }

                var longestString = valuePair.stream().map(Tuple::getA).max(Comparator.comparingInt(String::length)).orElse("");
                int width = font.width(longestString);

                for (var kv : valuePair) {
                    lineIndex++;
                    int y = 2 + lineIndex * font.lineHeight - 1;
                    if (renderBackground) {
                        GuiComponent.fill(ps, 0, y, width + font.width(kv.getB()) + diffWidth, y + font.lineHeight, 0x88000000);
                    }
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
