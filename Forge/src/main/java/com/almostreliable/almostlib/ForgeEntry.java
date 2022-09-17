package com.almostreliable.almostlib;

import net.minecraftforge.fml.common.Mod;

@Mod(BuildConfig.MOD_ID)
public class ForgeEntry {

    public ForgeEntry() {
        AlmostLib.PLATFORM.initRegistration(AlmostLib.ITEMS);
    }

}
