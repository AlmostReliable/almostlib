package testmod.gametest;

import com.almostreliable.almostlib.gametest.GameTestProvider;
import com.google.auto.service.AutoService;
import net.minecraft.core.BlockPos;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import testmod.TestMod;

import java.util.concurrent.atomic.AtomicBoolean;

@AutoService(GameTestProvider.class)
public class CapInvalidationTest implements GameTestProvider {

    private static final BlockPos POS = new BlockPos(1, 2, 1);

    @GameTest
    public void createEnergyGenerator(GameTestHelper helper) {
        helper.setBlock(POS, TestMod.ENERGY_BATTERY.get());

        BlockEntity blockEntity = helper.getBlockEntity(POS);
        if (blockEntity == null) {
            helper.fail("Block entity is not present!");
            return;
        }

        var cap = blockEntity.getCapability(ForgeCapabilities.ENERGY);
        AtomicBoolean invalidated = new AtomicBoolean(false);
        if (cap.isPresent()) {
            cap.addListener($ -> invalidated.set(true));
        }

        helper.setBlock(POS, Blocks.AIR);

        helper.succeedIf(() -> {
            if (!invalidated.get()) {
                helper.fail("Energy container was not invalidated!");
            }
        });
    }
}
