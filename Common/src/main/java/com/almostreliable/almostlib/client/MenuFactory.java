package com.almostreliable.almostlib.client;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.block.entity.BlockEntity;

@FunctionalInterface
public interface MenuFactory<M extends AbstractContainerMenu> {
    M apply(int id, Inventory inventory, FriendlyByteBuf buffer);

    @FunctionalInterface
    interface ForBlockEntity<M extends AbstractContainerMenu, BE extends BlockEntity> {
        M apply(int id, BE blockEntity);
    }

    @FunctionalInterface
    interface ForBlockEntityAndInventory<M extends AbstractContainerMenu, BE extends BlockEntity> {
        M apply(int id, Inventory inventory, BE blockEntity);
    }
}
