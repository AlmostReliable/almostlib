package com.almostreliable.almostlib.menu;

import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.Slot;

import java.util.function.Function;

public interface PlayerInventoryHost {

    /**
     * Returns the height offset at which the player inventory should start relative to the top of the screen.
     *
     * @return The height in pixels.
     */
    int getHeightOffset();

    /**
     * Adds the player inventory slots to the menu.
     *
     * @param slotAddFunction The function to add the slots with.
     * @param inventory       The player inventory.
     */
    default void addPlayerInventorySlots(Function<Slot, Slot> slotAddFunction, Inventory inventory) {
        // main inventory
        for (int row = 0; row < 3; row++) {
            for (int column = 0; column < 9; column++) {
                slotAddFunction.apply(new Slot(inventory, column + row * 9 + 9, 8 + column * 18, getHeightOffset() + row * 18));
            }
        }

        // hotbar
        for (int column = 0; column < 9; column++) {
            slotAddFunction.apply(new Slot(inventory, column, 8 + column * 18, getHeightOffset() + 58));
        }
    }
}
