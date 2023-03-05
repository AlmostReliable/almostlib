package com.almostreliable.almostlib.client.rendering;

import com.almostreliable.almostlib.util.Area;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.GuiComponent;

import java.util.ArrayDeque;
import java.util.Deque;

public class ScissorStack {

    private static final Deque<Scissor> QUEUE = new ArrayDeque<>();
    private static float renderScale = 1.0f;
    private static int xTranslation = 0;
    private static int yTranslation = 0;

    /**
     * Set the render scale. This will be applied to all scissor areas. This is useful for when you want to render something in a different scale than the actual scale.
     *
     * @param scale The scale to render at.
     */
    public static void setRenderScale(float scale) {
        renderScale = scale;
    }

    public static void resetRenderScale() {
        renderScale = 1.0f;
    }

    /**
     * Set the translation for the scissor stack. This will be applied to all scissor areas. This is useful for when you want to render something in a different position than the actual position.
     * <p>
     * Pushing a scissor area will also apply the translation.
     *
     * @param x The x translation.
     * @param y The y translation.
     */
    public static void setTranslation(int x, int y) {
        xTranslation = x;
        yTranslation = y;
    }

    public static void resetTranslation() {
        xTranslation = 0;
        yTranslation = 0;
    }

    /**
     * Enable scissor test for the given position. This will also apply the current render scale, which can be changed with {@link #setRenderScale(float)} and a custom translation, which can be changed with {@link #setTranslation(int, int)}.
     * <p>
     * After rendering in current scissor, {@link #pop()} must be called.
     *
     * @param area The area to enable scissor for.
     */
    public static void push(Area area) {
        push(area.getX(), area.getY(), area.getWidth(), area.getHeight());
    }

    /**
     * Enable scissor test for the given position. This will also apply the current render scale, which can be changed with {@link #setRenderScale(float)} and a custom translation, which can be changed with {@link #setTranslation(int, int)}.
     * <p>
     * After rendering in current scissor, {@link #pop()} must be called.
     *
     * @param x      The x position.
     * @param y      The y position.
     * @param width  The width.
     * @param height The height.
     */
    public static void push(int x, int y, int width, int height) {
        Scissor scissor = new Scissor(x + xTranslation, y + yTranslation, width, height);
        if (!QUEUE.isEmpty()) {
            scissor = scissor.intersect(QUEUE.peek());
        }
        QUEUE.push(scissor);
        scissor.enable();
    }

    public static void pop() {
        QUEUE.pop();
        Scissor scissor = QUEUE.peek();
        if (scissor != null) {
            scissor.enable();
        } else {
            RenderSystem.disableScissor();
        }
    }

    private record Scissor(int x, int y, int width, int height) {

        private void enable() {
            GuiComponent.enableScissor(x, y, (int) (x + width * renderScale), (int) (y + height * renderScale));
        }

        public Scissor intersect(Scissor other) {
            int x1 = Math.max(x, other.x);
            int y1 = Math.max(y, other.y);
            int x2 = Math.min(x + width, other.x + other.width);
            int y2 = Math.min(y + height, other.y + other.height);
            return new Scissor(x1, y1, x2 - x1, y2 - y1);
        }
    }
}
