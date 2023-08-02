package testmod;

import com.almostreliable.almostlib.AlmostLib;
import com.almostreliable.almostlib.gametest.GameTestLoader;
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
