package com.almostreliable.almostlib.client.gui;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.components.events.GuiEventListener;

@Environment(EnvType.CLIENT)
public interface AlmostGuiEventListener<T extends WidgetData> extends GuiEventListener {

    int LEFT_MOUSE_BUTTON = 0;
    int RIGHT_MOUSE_BUTTON = 1;
    int MIDDLE_MOUSE_BUTTON = 2;

    T getData();

    /**
     * Will be called when the mouse is clicked on this widget.
     * <p>
     * On default this method will be called when the widget is active, visible and {@link #isValidMouseClickButton(int)} returns true.
     * Widgets may override this behavior through {@link #mouseClicked(double, double, int)}
     *
     * @param mouseX
     * @param mouseY
     * @param mouseButton
     */
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
        if (getData().isActive() && getData().isVisible() && isValidMouseClickButton(mouseButton)) {
            onDrag(mouseX, mouseY, dragX, dragY);
            return true;
        }
        return false;
    }

    @Override
    default boolean isMouseOver(double mouseX, double mouseY) {
        return getData().isActive() && getData().isVisible() && getData().inBounds(mouseX, mouseY);
    }
    //endregion
}
