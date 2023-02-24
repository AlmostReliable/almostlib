package com.almostreliable.almostlib.client.gui;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.client.sounds.SoundManager;
import net.minecraft.sounds.SoundEvents;

/**
 * Implemented by widgets that can listen to several GUI interactions.
 *
 * @param <T> The type of data this widget uses.
 */
@Environment(EnvType.CLIENT)
public interface AlmostGuiEventListener<T extends WidgetData> extends GuiEventListener {

    // mouse buttons
    int LMB = 0;
    int RMB = 1;
    int MMB = 2;

    T getData();

    /**
     * Called when the widget is clicked.
     * <p>
     * By default, this method will be called when the widget is active, visible and {@link #isValidMouseClickButton(int)} returns true.
     * Widgets may override this behavior with {@link #mouseClicked(double, double, int)}.
     * <p>
     * The click will emit a sound by default. This can be disabled by overriding {@link #playClickSound(SoundManager, int)}.
     *
     * @param mouseX      The x position of the mouse.
     * @param mouseY      The y position of the mouse.
     * @param mouseButton The mouse button that was clicked.
     */
    default void onClick(double mouseX, double mouseY, int mouseButton) {}

    /**
     * Called when the widget is released.
     * <p>
     * By default, this method will be called when {@link #isValidMouseClickButton(int)} returns true.
     * Widgets may override this behavior with {@link #mouseReleased(double, double, int)}.
     *
     * @param mouseX      The x position of the mouse.
     * @param mouseY      The y position of the mouse.
     * @param mouseButton The mouse button that was released.
     */
    default void onRelease(double mouseX, double mouseY, int mouseButton) {}

    /**
     * Called when the mouse is dragged.
     * <p>
     * By default, this method will be called when the widget is active, visible and {@link #isValidMouseClickButton(int)} returns true.
     * Widgets may override this behavior with {@link #mouseDragged(double, double, int, double, double)}.
     *
     * @param mouseX The x position of the mouse.
     * @param mouseY The y position of the mouse.
     * @param dragX  The x direction the mouse has been dragged in.
     * @param dragY  The y direction the mouse has been dragged in.
     */
    default void onDrag(double mouseX, double mouseY, double dragX, double dragY) {}

    /**
     * Defines which mouse buttons are valid click buttons for this widget.
     * <ul>
     *     <li>0 = Left mouse button</li>
     *     <li>1 = Right mouse button</li>
     *     <li>2 = Middle mouse button</li>
     * </ul>
     *
     * @param mouseButton The mouse button that was clicked.
     * @return True when the mouse button is valid, false otherwise.
     */
    default boolean isValidMouseClickButton(int mouseButton) {
        return mouseButton == LMB;
    }

    /**
     * Plays a sound when the widget is clicked.
     * <p>
     * By default, this method will play {@link SoundEvents#UI_BUTTON_CLICK}.
     *
     * @param soundManager The sound manager.
     * @param mouseButton  The mouse button that was clicked.
     */
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
            return true;
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
