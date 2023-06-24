package com.almostreliable.almostlib.fabric;

import com.almostreliable.almostlib.AlmostLib;
import com.almostreliable.almostlib.gametest.GameTestLoader;
import net.fabricmc.api.ModInitializer;

public class FabricInitializer implements ModInitializer {

    @Override
    public void onInitialize() {
        AlmostLib.initNetworkHandler();

        var gametests = System.getProperty("fabric-api.gametest");
        if ("1".equals(gametests) || "true".equals(gametests)) {
            GameTestLoader.load();
        }
    }
}
