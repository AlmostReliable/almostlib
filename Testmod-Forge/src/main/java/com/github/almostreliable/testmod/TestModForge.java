package com.github.almostreliable.testmod;

import com.github.almostreliable.lib.AlmostLib;
import net.minecraftforge.fml.common.Mod;

@Mod(TestModCommon.MODID)
public class TestModForge {
    public TestModForge() {
        System.out.println("ERIK");
        AlmostLib.LOG.info("Hello from TestModForge");
        TestModCommon.MANAGER.init();
        TestModCommon.MANAGER.initClient();
    }
}
