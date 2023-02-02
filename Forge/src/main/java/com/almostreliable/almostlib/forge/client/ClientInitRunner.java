package com.almostreliable.almostlib.forge.client;

import com.almostreliable.almostlib.client.ClientInit;
import com.almostreliable.almostlib.client.ScreenFactory;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.MenuAccess;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

public class ClientInitRunner {

    public static void run(ClientInit clientInit) {
        Holder h = new Holder(clientInit);
        IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
        bus.addListener(h::onClientInit);
        bus.addListener(h::onRendererInit);
    }

    private record Holder(ClientInit clientInit) {

        public void onClientInit(FMLClientSetupEvent e) {
            e.enqueueWork(() -> {
                clientInit.onMenuScreensInit(new ClientInit.ScreenFactoryInit() {
                    @Override
                    public <T extends AbstractContainerMenu, U extends Screen & MenuAccess<T>> void register(MenuType<? extends T> menuType, ScreenFactory<T, U> screenFactory) {
                        //noinspection NullableProblems
                        MenuScreens.register(menuType, screenFactory::create);
                    }
                });

                clientInit.onClientInit();
            });
        }

        public void onRendererInit(EntityRenderersEvent.RegisterRenderers event) {
            clientInit.onEntityRendererInit(event::registerEntityRenderer);
            clientInit.onBlockEntityRendererInit(event::registerBlockEntityRenderer);
        }
    }
}
