package testmod.gametest;

import com.almostreliable.almostlib.gametest.AlmostGameTestHelper;
import com.almostreliable.almostlib.gametest.GameTestProvider;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.HopperBlock;
import net.minecraft.world.level.block.entity.HopperBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import testmod.TestMod;
import testmod.block.StorageBlock;

public class ItemContainerTests implements GameTestProvider {

    private static final BlockPos POS_1 = new BlockPos(1, 2, 1);
    private static final BlockPos POS_2 = POS_1.east();
    private static final ItemStack TRANSFER_STACK = new ItemStack(Items.DIAMOND, 3);
    private static final int HOPPER_COOLDOWN = 8;

    @GameTest(setupTicks = 2 * HOPPER_COOLDOWN + 1)
    public void itemTransfer(AlmostGameTestHelper helper) {
        helper.runAtTickTime(0, () -> {
            helper.setBlock(POS_1, TestMod.STORAGE_BLOCK.get());

            BlockState hopperState = Blocks.HOPPER.defaultBlockState()
                .setValue(HopperBlock.FACING, Direction.WEST);
            helper.setBlock(POS_2, hopperState);

            HopperBlockEntity hopper = helper.getBlockEntity(POS_2, HopperBlockEntity.class);
            hopper.setItem(0, TRANSFER_STACK.copy());
        });

        helper.succeedOnTickWhen(2 * HOPPER_COOLDOWN + 1, () -> {
            var storageBlock = helper.getBlockEntity(POS_1, StorageBlock.Entity.class);
            HopperBlockEntity hopper = helper.getBlockEntity(POS_2, HopperBlockEntity.class);

            if (TRANSFER_STACK.test(hopper.getItem(0))) {
                helper.fail("Hopper still holds items it should have transferred!");
            }
            if (!TRANSFER_STACK.test(storageBlock.getInventory().getItem(0))) {
                helper.fail("Storage block does not hold items it should have received!");
            }
        });
    }
}
