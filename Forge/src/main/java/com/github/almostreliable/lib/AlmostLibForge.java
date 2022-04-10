package com.github.almostreliable.lib;

import com.github.almostreliable.lib.registry.RegistryManager;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLConstructModEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(AlmostLib.MOD_ID)
public class AlmostLibForge {
    public AlmostLibForge() {
        AlmostLibCommon.init();
        FMLJavaModLoadingContext.get().getModEventBus().addListener(AlmostLibForge::onCommonSetup);
    }

    private static void onCommonSetup(FMLConstructModEvent event) {
        for (RegistryManager manager : AlmostLibCommon.MANAGERS) {
            manager.init();
        }
    }
}
