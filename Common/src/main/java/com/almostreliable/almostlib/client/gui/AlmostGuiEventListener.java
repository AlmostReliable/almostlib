package com.almostreliable.almostlib.client.gui;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.client.sounds.SoundManager;
import net.minecraft.sounds.SoundEvents;

@Environment(EnvType.CLIENT)
public interface AlmostGuiEventListener<T extends WidgetData> extends GuiEventListener {

    // mouse buttons
    int LMB = 0;
    int RMB = 1;
    int MMB = 2;

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
        return mouseButton == LMB;
    }

    default void playClickSound(SoundManager soundManager, int mouseButton) {
        soundManager.play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1.0f));
    }

    //region GuiEventListener implementation
    @Override
    default boolean mouseClicked(double mouseX, double mouseY, int mouseButton) {
        if (getData().isActive() && getData().isVisible() && getData().isHovered() && isValidMouseClickButton(mouseButton)) {
            playClickSound(Minecraft.getInstance().getSoundManager(), mouseButton);
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
