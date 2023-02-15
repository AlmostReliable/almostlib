package com.almostreliable.almostlib.client.gui;

import com.mojang.blaze3d.vertex.PoseStack;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.components.Widget;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarratableEntry;
import net.minecraft.client.gui.narration.NarratedElementType;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;

@Environment(EnvType.CLIENT)
public interface AlmostWidget<T extends WidgetData> extends NarratableEntry, WidgetDataProvider<T>, GuiEventListener, Widget {

    int LEFT_MOUSE_BUTTON = 0;
    int RIGHT_MOUSE_BUTTON = 1;
    int MIDDLE_MOUSE_BUTTON = 2;

    void render(PoseStack stack, int offsetX, int offsetY, int mouseX, int mouseY, float delta);

    @Override
    default void render(PoseStack poseStack, int mouseX, int mouseY, float delta) {
        // Makes it possible to directly use this widget in vanilla screens without wrapping it. Not recommended to override this method.
        // Please use render(PoseStack, int, int, int, int, float) instead
        render(poseStack, 0, 0, mouseX, mouseY, delta);
    }

    default boolean inBounds(double mouseX, double mouseY) {
        return getData().getX() <= mouseX && mouseX <= getData().getRight() && getData().getY() <= mouseY && mouseY <= getData().getBottom();
    }

    default void onClick(double mouseX, double mouseY, int mouseButton) {

    }

    default void onRelease(double mouseX, double mouseY, int mouseButton) {

    }

    default void onDrag(double mouseX, double mouseY, double dragX, double dragY) {

    }

    /**
     * Override this method to allow other mouse buttons to be used for this widget
     * <ul>
     *     <li>0 = Left mouse button</li>
     *     <li>1 = Right mouse button</li>
     *     <li>2 = Middle mouse button</li>
     * </ul>
     *
     * @param mouseButton The mouse button that was clicked
     * @return True if the mouse button is valid, false otherwise
     */
    default boolean isValidMouseClickButton(int mouseButton) {
        return mouseButton == LEFT_MOUSE_BUTTON;
    }

    //region GuiEventListener implementation
    @Override
    default boolean mouseClicked(double mouseX, double mouseY, int mouseButton) {
        if (getData().isActive() && getData().isVisible() && getData().isHovered() && isValidMouseClickButton(mouseButton)) {
            onClick(mouseX, mouseY, mouseButton);
            return true;
        }
        return false;
    }

    @Override
    default boolean mouseReleased(double mouseX, double mouseY, int mouseButton) {
        if (isValidMouseClickButton(mouseButton)) {
            onRelease(mouseX, mouseY, mouseButton);
        }
        return false;
    }

    @Override
    default boolean mouseDragged(double mouseX, double mouseY, int mouseButton, double dragX, double dragY) {
        // TODO check also for active, visible and hovered?
        if (mouseButton == LEFT_MOUSE_BUTTON) {
            onDrag(mouseX, mouseY, dragX, dragY);
        }
        return false;
    }

    @Override
    default boolean isMouseOver(double mouseX, double mouseY) {
        return getData().isActive() && getData().isVisible() && inBounds(mouseX, mouseY);
    }
    //endregion

    @Override
    default NarrationPriority narrationPriority() {
        if (getData().isHovered()) {
            return NarrationPriority.HOVERED;
        }
        return NarrationPriority.NONE;
    }

    @Override
    default void updateNarration(NarrationElementOutput narrationElementOutput) {
        if (getData().isActive() && getData().isHovered()) {
            narrationElementOutput.add(NarratedElementType.USAGE, Component.translatable("narration.button.usage.hovered"));
        }
    }

    @Override
    default boolean isActive() {
        return getData().isActive();
    }
}
