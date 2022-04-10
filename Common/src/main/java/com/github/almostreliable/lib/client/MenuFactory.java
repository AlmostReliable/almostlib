package com.github.almostreliable.lib.client;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;

@FunctionalInterface
public interface MenuFactory<M extends AbstractContainerMenu> {
    M apply(int id, Inventory inventory, FriendlyByteBuf buffer);
}
