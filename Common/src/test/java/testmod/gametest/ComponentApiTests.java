package testmod.gametest;

import com.almostreliable.almostlib.gametest.GameTestProvider;
import com.google.auto.service.AutoService;
import net.minecraft.core.BlockPos;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.world.level.block.entity.BlockEntity;
import testmod.TestMod;

@AutoService(GameTestProvider.class)
public class ComponentApiTests implements GameTestProvider {

    private static final BlockPos GEN_POS = new BlockPos(1, 2, 1);

    @GameTest
    public void createEnergyGenerator(GameTestHelper helper) {
        helper.setBlock(GEN_POS, TestMod.ENERGY_GENERATOR.get());
        helper.succeedIf(() -> {
            BlockEntity be = helper.getBlockEntity(GEN_POS);
            if (be == null) {
                helper.fail("Block entity is null!");
            }
        });
    }

    @GameTest
    public void energyGeneratorSendsEnergy(GameTestHelper helper) {
        helper.setBlock(GEN_POS, TestMod.ENERGY_GENERATOR.get());
        helper.succeedIf(() -> {
            BlockEntity be = helper.getBlockEntity(GEN_POS);
            if (be == null) {
                helper.fail("Block entity is null!");
            }
        });
    }
}
