package com.github.almostreliable.lib.registry;

import com.github.almostreliable.lib.client.ScreenFactory;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.MenuAccess;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

public abstract class ClientManager {
    protected static final List<BlockEntityRendererHolder<?>> blockEntityRenderers = new ArrayList<>();
    protected final Map<MenuType<?>, ScreenFactory<?, ?>> screens = new ConcurrentHashMap<>();

    public <BE extends BlockEntity> void registerBlockEntityRenderer(BlockEntityType<BE> type, Function<BlockEntityRendererProvider.Context, BlockEntityRenderer<BE>> renderer) {
        BlockEntityRendererHolder<BE> blockEntityRendererHolder = new BlockEntityRendererHolder<>(type, renderer);
        blockEntityRenderers.add(blockEntityRendererHolder);
    }

    public <M extends AbstractContainerMenu, S extends Screen & MenuAccess<M>> void registerScreen(MenuType<? extends M> menuType, ScreenFactory<M, S> screenFactory) {
        screens.put(menuType, screenFactory);
    }

    protected abstract void init();

    protected record BlockEntityRendererHolder<BE extends BlockEntity>(
            BlockEntityType<BE> type,
            Function<BlockEntityRendererProvider.Context, BlockEntityRenderer<BE>> provider) {}
}
