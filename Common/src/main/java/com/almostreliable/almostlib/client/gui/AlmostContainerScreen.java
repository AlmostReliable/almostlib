package com.almostreliable.almostlib.client.gui;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;

/**
 * A basic implementation of an {@link AbstractContainerScreen} in order to have native
 * compatibility with widgets implementing {@link AlmostGuiEventListener}.
 * @param <M> The type of the container menu.
 */
@Environment(EnvType.CLIENT)
public abstract class AlmostContainerScreen<M extends AbstractContainerMenu> extends AbstractContainerScreen<M> {

    protected AlmostContainerScreen(M menu, Inventory inventory, Component component) {
        super(menu, inventory, component);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int mouseButton) {
        if (mouseButton == AlmostGuiEventListener.RMB) {
            for (var listener : children()) {
                if (listener.mouseClicked(mouseX, mouseY, mouseButton)) {
                    setFocused(listener);
                    setDragging(true);
                    return true;
                }
            }
        }
        return super.mouseClicked(mouseX, mouseY, mouseButton);
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int mouseButton, double dragX, double dragY) {
        return (isDragging() && getFocused() instanceof AlmostGuiEventListener<?> focused &&
                    focused.mouseDragged(mouseX, mouseY, mouseButton, dragX, dragY)) ||
            super.mouseDragged(mouseX, mouseY, mouseButton, dragX, dragY);
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int mouseButton) {
        setDragging(false);
        return getChildAt(mouseX, mouseY)
            .filter(child -> child instanceof AlmostGuiEventListener<?> listener && listener.mouseReleased(mouseX, mouseY, mouseButton))
            .isPresent() ||
            super.mouseReleased(mouseX, mouseY, mouseButton);
    }
}
