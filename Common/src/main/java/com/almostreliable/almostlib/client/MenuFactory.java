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
        M apply(int id, Inventory inventory, BE blockEntity);
    }

    @FunctionalInterface
    interface ForCustom<M extends AbstractContainerMenu, BE extends BlockEntity> {

        M apply(int id, Inventory inventory, BE blockEntity, FriendlyByteBuf buffer);
    }
}
