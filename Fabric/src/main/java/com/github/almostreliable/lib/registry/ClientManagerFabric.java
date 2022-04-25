package com.github.almostreliable.lib.registry;

import com.github.almostreliable.lib.client.ScreenFactory;
import net.fabricmc.fabric.api.client.rendering.v1.BlockEntityRendererRegistry;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.MenuAccess;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;

import java.util.function.Function;

public class ClientManagerFabric extends ClientManager {
    @Override
    public <BE extends BlockEntity> void registerBlockEntityRenderer(BlockEntityType<BE> type, Function<BlockEntityRendererProvider.Context, BlockEntityRenderer<BE>> renderer) {
        BlockEntityRendererRegistry.register(type, renderer::apply);
    }

    @Override
    public <M extends AbstractContainerMenu, S extends Screen & MenuAccess<M>> void registerScreen(MenuType<? extends M> menuType, ScreenFactory<M, S> screenFactory) {
        MenuScreens.register(menuType, screenFactory::create);
    }
}
