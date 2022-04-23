package com.github.almostreliable.testmod;

import net.minecraft.gametest.framework.GameTestRegistry;
import net.minecraftforge.event.RegisterGameTestsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = TestModCommon.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class GameTestForgeEntry {
    @SubscribeEvent
    public static void onRegisterGameTests(RegisterGameTestsEvent event) {
        GameTestRegistry.register(RegistryGameTest.class);
    }
}
