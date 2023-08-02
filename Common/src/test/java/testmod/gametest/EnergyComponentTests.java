package testmod.gametest;

import com.almostreliable.almostlib.component.EnergyContainer;
import com.almostreliable.almostlib.gametest.GameTestProvider;
import com.google.auto.service.AutoService;
import net.minecraft.core.BlockPos;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import testmod.TestMod;
import testmod.block.EnergyBatteryBlock;
import testmod.block.EnergyGeneratorBlock;

@AutoService(GameTestProvider.class)
public class EnergyComponentTests implements GameTestProvider {

    private static final BlockPos POS_1 = new BlockPos(1, 2, 1);
    private static final BlockPos POS_2 = new BlockPos(1, 3, 1);

    @GameTest(setupTicks = 3)
    public void createEnergyGenerator(GameTestHelper helper) {
        helper.runAtTickTime(0, () -> {
            helper.setBlock(POS_1, TestMod.ENERGY_GENERATOR.get());
            helper.setBlock(POS_2, TestMod.ENERGY_BATTERY.get());
        });

        helper.succeedOnTickWhen(3, () -> {
            if (!(helper.getBlockEntity(POS_2) instanceof EnergyBatteryBlock.Entity battery)) {
                helper.fail("Block entity is not an energy battery!");
                return;
            }

            EnergyContainer energyContainer = battery.getEnergyContainer(null);
            if (energyContainer == null) {
                helper.fail("Energy container is null!");
                return;
            }

            long expectedEnergy = EnergyGeneratorBlock.Entity.ENERGY_GEN_RATE * 3;
            long actualEnergy = energyContainer.getAmount();
            if (actualEnergy != expectedEnergy) {
                helper.fail("Energy container does not have the correct amount of energy! Expected: " + expectedEnergy + ", Actual: " + actualEnergy);
            }
        });
    }
}
