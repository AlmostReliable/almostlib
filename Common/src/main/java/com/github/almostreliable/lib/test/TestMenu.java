package com.github.almostreliable.lib.test;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;

public class TestMenu extends AbstractContainerMenu {
    protected TestMenu(int id) {
        super(TestRegistry.TEST_MENU.get(), id);
    }

    @Override
    public boolean stillValid(Player entity) {
        return true;
    }
}
