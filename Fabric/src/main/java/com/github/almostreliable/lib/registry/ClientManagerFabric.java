package com.github.almostreliable.lib.registry;

import com.github.almostreliable.lib.Utils;
import net.fabricmc.fabric.api.client.rendering.v1.BlockEntityRendererRegistry;
import net.minecraft.client.gui.screens.MenuScreens;

public class ClientManagerFabric extends ClientManager {
    @Override
    protected void init() {
        screens.forEach((menuType, factory) -> {
            MenuScreens.register(Utils.cast(menuType), factory::create);
        });

        blockEntityRenderers.forEach(holder -> {
            BlockEntityRendererRegistry.register(Utils.cast(holder.type()), holder.provider()::apply);
        });
    }
}
