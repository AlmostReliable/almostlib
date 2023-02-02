package com.almostreliable.almostlib.fabric.client;

import com.almostreliable.almostlib.client.ClientInit;
import com.almostreliable.almostlib.client.ScreenFactory;
import net.fabricmc.fabric.api.client.rendering.v1.BlockEntityRendererRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.MenuAccess;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;

public class ClientInitRunner {

    public static void run(ClientInit clientInit) {
        clientInit.onEntityRendererInit(EntityRendererRegistry::register);
        clientInit.onBlockEntityRendererInit(BlockEntityRendererRegistry::register);
        clientInit.onMenuScreensInit(new ClientInit.ScreenFactoryInit() {
            @Override
            public <T extends AbstractContainerMenu, U extends Screen & MenuAccess<T>> void register(MenuType<? extends T> menuType, ScreenFactory<T, U> screenFactory) {
                MenuScreens.register(menuType, screenFactory::create);
            }
        });
        clientInit.onClientInit();
    }
}
