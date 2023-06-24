package testmod;

import net.fabricmc.api.ModInitializer;

public class TestFabricInitializer implements ModInitializer {

    @Override
    public void onInitialize() {
        TestMod.init();
    }
}
