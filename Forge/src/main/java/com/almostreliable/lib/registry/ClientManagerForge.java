package com.almostreliable.lib.registry;

import com.almostreliable.lib.client.ScreenFactory;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.MenuAccess;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderers;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

import java.util.function.Function;

public class ClientManagerForge extends ClientManager {
    @Override
    public <BE extends BlockEntity> void registerBlockEntityRenderer(BlockEntityType<BE> type, Function<BlockEntityRendererProvider.Context, BlockEntityRenderer<BE>> renderer) {
        BlockEntityRenderers.register(type, renderer::apply);
    }

    @Override
    public <M extends AbstractContainerMenu, S extends Screen & MenuAccess<M>> void registerScreen(MenuType<? extends M> menuType, ScreenFactory<M, S> screenFactory) {
        MenuScreens.register(menuType, screenFactory::create);
    }

//    @Override
//    protected void init() {
//        var eventBus = FMLJavaModLoadingContext.get().getModEventBus();
//        eventBus.addListener(this::handleFMLClient);
//    }

    private void handleFMLClient(FMLClientSetupEvent event) {
//        event.enqueueWork(() -> {
//            screens.forEach((menuType, factory) -> {
//                MenuScreens.register(Utils.cast(menuType), factory::create);
//            });
//
//            blockEntityRenderers.forEach(holder -> {
//                BlockEntityRenderers.register(Utils.cast(holder.type()), holder.provider()::apply);
//            });
//        });
    }
}
