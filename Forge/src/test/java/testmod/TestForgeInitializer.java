package testmod;

import net.minecraftforge.fml.common.Mod;

@Mod("testmod")
public class TestForgeInitializer {

    public TestForgeInitializer() {
        TestMod.init();
    }
}
