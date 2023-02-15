package com.almostreliable.almostlib.forge;

import com.almostreliable.almostlib.BuildConfig;
import com.almostreliable.almostlib.TestEntry;
import net.minecraftforge.data.event.GatherDataEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(BuildConfig.MOD_ID)
public class ForgeInitializer {

    public ForgeInitializer() {
        TestEntry.init();
        TestEntry.MANAGER.initRegistriesToLoader();
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::onDataGen);
    }

    public void onDataGen(GatherDataEvent e) {
        TestEntry.MANAGER.dataGen().collectProviders(e.getGenerator());
    }
}
