package testmod.gametest;

import com.almostreliable.almostlib.gametest.AlmostGameTestHelper;
import com.almostreliable.almostlib.gametest.GameTestProvider;
import net.minecraft.core.BlockPos;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import testmod.TestMod;
import testmod.block.EnergyBatteryBlock;

import java.util.concurrent.atomic.AtomicBoolean;

public class CapInvalidationTest implements GameTestProvider {

    private static final BlockPos POS = new BlockPos(1, 2, 1);

    @GameTest
    public void createEnergyGenerator(AlmostGameTestHelper helper) {
        helper.setBlock(POS, TestMod.ENERGY_BATTERY.get());

        var battery = helper.getBlockEntity(POS, EnergyBatteryBlock.Entity.class);
        var cap = battery.getCapability(ForgeCapabilities.ENERGY);

        AtomicBoolean invalidated = new AtomicBoolean(false);
        if (cap.isPresent()) {
            cap.addListener($ -> invalidated.set(true));
        }

        helper.succeedIf(() -> {
            helper.setBlock(POS, Blocks.AIR);

            if (!invalidated.get()) {
                helper.fail("Energy container was not invalidated!");
            }
        });
    }
}
