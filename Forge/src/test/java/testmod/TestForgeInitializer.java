package testmod;

import com.almostreliable.lib.AlmostLib;
import com.almostreliable.lib.gametest.GameTestLoader;
import net.minecraftforge.fml.common.Mod;
import testmod.gametest.CapInvalidationTest;

@Mod("testmod")
public class TestForgeInitializer {

    public TestForgeInitializer() {
        TestMod.init();

        if (AlmostLib.PLATFORM.isGameTestEnabled()) {
            GameTestLoader.registerProviders(CapInvalidationTest.class);
        }
    }
}
