package com.github.almostreliable.lib.test;

import com.github.almostreliable.lib.AlmostLib;
import net.minecraft.gametest.framework.GameTestRegistry;
import net.minecraftforge.event.RegisterGameTestsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = AlmostLib.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class GameTestForgeEntry {
    @SubscribeEvent
    public static void onRegisterGameTests(RegisterGameTestsEvent event) {
        GameTestRegistry.register(RegistryGameTest.class);
    }
}
