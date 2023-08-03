package testmod.gametest;

import com.almostreliable.almostlib.gametest.AlmostGameTestHelper;
import com.almostreliable.almostlib.gametest.GameTestProvider;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.world.Container;
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
    private static final BlockPos POS_3 = POS_1.above();
    private static final ItemStack TRANSFER_STACK = new ItemStack(Items.DIAMOND, 3);
    private static final int HOPPER_COOLDOWN = 8;

    @GameTest(setupTicks = 2 * HOPPER_COOLDOWN + 1)
    public void simpleInsertion(AlmostGameTestHelper helper) {
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

            if (!hopper.isEmpty()) {
                helper.fail("Hopper didn't transfer the expected amount of items!");
            }
            if (!TRANSFER_STACK.test(storageBlock.getInventory().getItem(0))) {
                helper.fail("Storage block didn't receive the expected amount of items!");
            }
        });
    }

    @GameTest(setupTicks = 2 * HOPPER_COOLDOWN + 1)
    public void stackingInsertion(AlmostGameTestHelper helper) {
        helper.runAtTickTime(0, () -> {
            helper.setBlock(POS_1, TestMod.STORAGE_BLOCK.get());
            var storageBlock = helper.getBlockEntity(POS_1, StorageBlock.Entity.class);
            Container storageInventory = storageBlock.getInventory();
            storageInventory.setItem(0, new ItemStack(Items.DIAMOND, 62));
            storageInventory.setItem(1, new ItemStack(Items.APPLE, 16));

            BlockState hopperState = Blocks.HOPPER.defaultBlockState()
                .setValue(HopperBlock.FACING, Direction.WEST);
            helper.setBlock(POS_2, hopperState);

            HopperBlockEntity hopper = helper.getBlockEntity(POS_2, HopperBlockEntity.class);
            hopper.setItem(0, TRANSFER_STACK.copy());
        });

        helper.succeedOnTickWhen(2 * HOPPER_COOLDOWN + 1, () -> {
            var storageBlock = helper.getBlockEntity(POS_1, StorageBlock.Entity.class);
            Container storageInventory = storageBlock.getInventory();
            HopperBlockEntity hopper = helper.getBlockEntity(POS_2, HopperBlockEntity.class);

            if (!hopper.isEmpty()) {
                helper.fail("Hopper didn't transfer the expected amount of items!");
            }
            if (!storageInventory.getItem(0).test(new ItemStack(Items.DIAMOND, 64)) ||
                !storageInventory.getItem(1).test(new ItemStack(Items.APPLE, 16)) ||
                !storageInventory.getItem(2).test(Items.DIAMOND.getDefaultInstance())) {
                helper.fail("Storage block didn't receive the expected amount of items!");
            }
        });
    }

    @GameTest(setupTicks = 2 * HOPPER_COOLDOWN + 1)
    public void blockingInsertion(AlmostGameTestHelper helper) {
        helper.runAtTickTime(0, () -> {
            helper.setBlock(POS_1, TestMod.STORAGE_BLOCK.get());
            var storageBlock = helper.getBlockEntity(POS_1, StorageBlock.Entity.class);
            Container storageInventory = storageBlock.getInventory();
            storageInventory.setItem(0, new ItemStack(Items.DIAMOND, 64));
            storageInventory.setItem(1, new ItemStack(Items.APPLE, 64));
            storageInventory.setItem(2, new ItemStack(Items.DIRT, 64));

            BlockState hopperState = Blocks.HOPPER.defaultBlockState()
                .setValue(HopperBlock.FACING, Direction.WEST);
            helper.setBlock(POS_2, hopperState);

            HopperBlockEntity hopper = helper.getBlockEntity(POS_2, HopperBlockEntity.class);
            hopper.setItem(0, TRANSFER_STACK.copy());
        });

        helper.succeedAtTickTime(2 * HOPPER_COOLDOWN + 1, () -> {
            var storageBlock = helper.getBlockEntity(POS_1, StorageBlock.Entity.class);
            Container storageInventory = storageBlock.getInventory();
            HopperBlockEntity hopper = helper.getBlockEntity(POS_2, HopperBlockEntity.class);

            if (!hopper.getItem(0).test(TRANSFER_STACK)) {
                helper.fail("Hopper transferred more items than it should have!");
            }
            if (!storageInventory.getItem(0).test(new ItemStack(Items.DIAMOND, 64)) ||
                !storageInventory.getItem(1).test(new ItemStack(Items.APPLE, 64)) ||
                !storageInventory.getItem(2).test(new ItemStack(Items.DIRT, 64))) {
                helper.fail("Storage block doesn't contain the expected items!");
            }
        });
    }

    @GameTest(setupTicks = 2 * HOPPER_COOLDOWN + 1)
    public void simpleExtraction(AlmostGameTestHelper helper) {
        helper.runAtTickTime(0, () -> {
            helper.setBlock(POS_3, TestMod.STORAGE_BLOCK.get());

            var storageBlock = helper.getBlockEntity(POS_3, StorageBlock.Entity.class);
            storageBlock.getInventory().setItem(0, TRANSFER_STACK.copy());

            BlockState hopperState = Blocks.HOPPER.defaultBlockState()
                .setValue(HopperBlock.FACING, Direction.DOWN);
            helper.setBlock(POS_1, hopperState);
        });

        helper.succeedOnTickWhen(2 * HOPPER_COOLDOWN + 1, () -> {
            var storageBlock = helper.getBlockEntity(POS_3, StorageBlock.Entity.class);
            HopperBlockEntity hopper = helper.getBlockEntity(POS_1, HopperBlockEntity.class);

            if (!storageBlock.getInventory().isEmpty()) {
                helper.fail("Storage block didn't transfer the expected amount of items!");
            }
            if (!TRANSFER_STACK.test(hopper.getItem(0))) {
                helper.fail("Hopper didn't receive the expected amount of items!");
            }
        });
    }
}
