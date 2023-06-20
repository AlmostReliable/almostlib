package com.almostreliable.almostlib.fabric.client;

import com.almostreliable.almostlib.AlmostManager;
import com.almostreliable.almostlib.client.ClientInit;
import com.almostreliable.almostlib.client.RenderLayerType;
import com.almostreliable.almostlib.client.ScreenFactory;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.client.rendering.v1.BlockEntityRendererRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.MenuAccess;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.level.block.Block;

import java.util.function.BiConsumer;
import java.util.function.Supplier;

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

    public static void run(Supplier<ClientInit> clientInitFactory) {
        run(clientInitFactory.get());
    }

    public static void run(ClientInit clientInit, AlmostManager manager) {
        run(clientInit);
        manager.blocks().setupRenderLayers(applyRenderTypes(BlockRenderLayerMap.INSTANCE::putBlock));
    }

    public static void run(Supplier<ClientInit> clientInitFactory, AlmostManager manager) {
        run(clientInitFactory.get(), manager);
    }

    public static BiConsumer<Block, RenderLayerType> applyRenderTypes(BiConsumer<Block, RenderType> consumer) {
        return (block, renderLayer) -> {
            RenderType renderType = switch (renderLayer) {
                case SOLID -> RenderType.solid();
                case CUTOUT -> RenderType.cutout();
                case CUTOUT_MIPPED -> RenderType.cutoutMipped();
                case TRANSLUCENT -> RenderType.translucent();
            };
            consumer.accept(block, renderType);
        };
    }
}
