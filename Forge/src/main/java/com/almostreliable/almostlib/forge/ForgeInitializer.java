package com.almostreliable.almostlib.forge;

import com.almostreliable.almostlib.AlmostLib;
import com.almostreliable.almostlib.BuildConfig;
import com.almostreliable.almostlib.gametest.GameTestLoader;
import net.minecraftforge.event.RegisterGameTestsEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(BuildConfig.MOD_ID)
public class ForgeInitializer {

    public ForgeInitializer() {
        AlmostLib.initNetworkHandler();
        FMLJavaModLoadingContext.get().getModEventBus().addListener(ForgeInitializer::onGameTestRun);
    }

    private static void onGameTestRun(RegisterGameTestsEvent event) {
        GameTestLoader.load();
    }
}
