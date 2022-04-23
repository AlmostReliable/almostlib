package com.github.almostreliable.lib.registry;

import com.github.almostreliable.lib.Utils;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderers;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

public class ClientManagerForge extends ClientManager {
    @Override
    protected void init() {
        var eventBus = FMLJavaModLoadingContext.get().getModEventBus();
        eventBus.addListener(this::handleFMLClient);
    }

    private void handleFMLClient(FMLClientSetupEvent event) {
        event.enqueueWork(() -> {
            screens.forEach((menuType, factory) -> {
                MenuScreens.register(Utils.cast(menuType), factory::create);
            });

            blockEntityRenderers.forEach(holder -> {
                BlockEntityRenderers.register(Utils.cast(holder.type()), holder.provider()::apply);
            });
        });
    }
}
