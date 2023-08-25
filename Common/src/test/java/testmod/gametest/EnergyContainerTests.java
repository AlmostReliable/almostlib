package testmod.gametest;

import com.almostreliable.lib.component.EnergyContainer;
import com.almostreliable.lib.component.impl.SimpleEnergyContainer;
import com.almostreliable.lib.gametest.AlmostGameTestHelper;
import com.almostreliable.lib.gametest.GameTestProvider;
import net.minecraft.core.BlockPos;
import net.minecraft.gametest.framework.GameTest;
import testmod.TestMod;
import testmod.block.EnergyBatteryBlock;
import testmod.block.EnergyGeneratorBlock;

public class EnergyContainerTests implements GameTestProvider {

    private static final BlockPos POS_1 = new BlockPos(1, 2, 1);
    private static final BlockPos POS_2 = POS_1.above();

    @GameTest
    public void energyTransferCap(AlmostGameTestHelper helper) {
        helper.setBlock(POS_1, TestMod.ENERGY_BATTERY.get());

        helper.succeedIf(() -> {
            var battery = helper.getBlockEntity(POS_1, EnergyBatteryBlock.Entity.class);

            SimpleEnergyContainer energyContainer = battery.getEnergyStorage();
            energyContainer.setMaxInsert(100);
            energyContainer.setMaxExtract(50);

            if (energyContainer.insert(300, false) != 100) {
                helper.fail("EnergyContainer didn't limit insertion correctly!");
            }
            if (energyContainer.extract(300, false) != 50) {
                helper.fail("EnergyContainer didn't limit extraction correctly!");
            }
        });
    }

    @GameTest
    public void energyCapacityCap(AlmostGameTestHelper helper) {
        helper.setBlock(POS_1, TestMod.ENERGY_BATTERY.get());

        helper.succeedIf(() -> {
            var battery = helper.getBlockEntity(POS_1, EnergyBatteryBlock.Entity.class);

            SimpleEnergyContainer energyContainer = battery.getEnergyStorage();
            energyContainer.setCapacity(100);

            if (energyContainer.insert(200, false) != 100) {
                helper.fail("EnergyContainer didn't cap energy correctly!");
            }
        });
    }

    @GameTest(setupTicks = 3)
    public void energyTransfer(AlmostGameTestHelper helper) {
        helper.runAtTickTime(0, () -> {
            helper.setBlock(POS_1, TestMod.ENERGY_GENERATOR.get());
            helper.setBlock(POS_2, TestMod.ENERGY_BATTERY.get());
        });

        helper.succeedOnTickWhen(3, () -> {
            var battery = helper.getBlockEntity(POS_2, EnergyBatteryBlock.Entity.class);

            EnergyContainer energyContainer = battery.getEnergyStorage();
            long expectedEnergy = EnergyGeneratorBlock.Entity.ENERGY_GEN_RATE * 3;
            long actualEnergy = energyContainer.getAmount();

            if (actualEnergy != expectedEnergy) {
                helper.fail("EnergyContainer does not have the correct amount of energy! Expected: " + expectedEnergy + ", Actual: " + actualEnergy);
            }
        });
    }
}
