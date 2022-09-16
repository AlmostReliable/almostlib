package com.almostreliable.almostlib;

import net.fabricmc.loader.api.FabricLoader;

public class AlmostLibPlatformFabric implements AlmostLibPlatform {

    @Override
    public Platform getPlatform() {
        return Platform.FABRIC;
    }

    @Override
    public boolean isModLoaded(String modId) {
        return FabricLoader.getInstance().isModLoaded(modId);
    }

    @Override
    public boolean isDevelopmentEnvironment() {
        return FabricLoader.getInstance().isDevelopmentEnvironment();
    }
}
