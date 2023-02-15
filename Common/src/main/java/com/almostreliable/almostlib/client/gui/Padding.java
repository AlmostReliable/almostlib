package com.almostreliable.almostlib.client.gui;

import com.almostreliable.almostlib.util.Area;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.renderer.Rect2i;

@Environment(EnvType.CLIENT)
public record Padding(int top, int right, int bottom, int left) {

    public static final Padding ZERO = new Padding(0, 0, 0, 0);

    public static Padding of(int top, int right, int bottom, int left) {
        return new Padding(top, right, bottom, left);
    }

    public static Padding of(int topBottom, int leftRight) {
        return new Padding(topBottom, leftRight, topBottom, leftRight);
    }

    public static Padding of(int all) {
        return new Padding(all, all, all, all);
    }

    public Area apply(Area rect) {
        return new Area.Simple(rect.getX() + left, rect.getY() + top, rect.getWidth() - left - right, rect.getHeight() - top - bottom);
    }
}
