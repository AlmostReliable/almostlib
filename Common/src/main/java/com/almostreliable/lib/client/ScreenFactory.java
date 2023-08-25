package com.almostreliable.lib.client;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.MenuAccess;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;

@Environment(EnvType.CLIENT)
@FunctionalInterface
public interface ScreenFactory<T extends AbstractContainerMenu, U extends Screen & MenuAccess<T>> {

    U create(T abstractContainerMenu, Inventory inventory, Component component);
}
