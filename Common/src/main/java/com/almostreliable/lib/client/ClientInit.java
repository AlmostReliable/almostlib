package com.almostreliable.lib.client;

import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.MenuAccess;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;

/**
 * Can be used to register client-only features. Implement this interface and register it with `ClientInitRunner` for the respective loader.<p>
 * => Fabric: {@link com.almostreliable.lib.fabric.client.ClientInitRunner} in {@link net.fabricmc.api.ClientModInitializer}<p>
 * => Forge: {@link com.almostreliable.lib.forge.client.ClientInitRunner} in mod constructor
 */
public interface ClientInit {

    default void onClientInit() {}

    default void onMenuScreensInit(ScreenFactoryInit event) {}

    default void onEntityRendererInit(EntityRendererRegisterInit event) {}

    default void onBlockEntityRendererInit(BlockEntityRendererInit event) {}

    @FunctionalInterface
    interface ScreenFactoryInit {

        <T extends AbstractContainerMenu, U extends Screen & MenuAccess<T>> void register(MenuType<? extends T> menuType, ScreenFactory<T, U> screenFactory);
    }

    @FunctionalInterface
    interface EntityRendererRegisterInit {

        <T extends Entity> void register(EntityType<? extends T> entityType, EntityRendererProvider<T> rendererProvider);
    }

    @FunctionalInterface
    interface BlockEntityRendererInit {

        <T extends BlockEntity> void register(BlockEntityType<? extends T> blockEntityType, BlockEntityRendererProvider<T> rendererProvider);
    }
}
