package com.github.almostreliable.lib;

import net.fabricmc.api.ModInitializer;

public class AlmostLibFabric implements ModInitializer {
    @Override
    public void onInitialize() {
        CommonClass.init();
    }
}
