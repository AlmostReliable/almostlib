package testmod.gametest;

import com.almostreliable.almostlib.gametest.GameTestProvider;
import com.almostreliable.almostlib.util.filter.BlockPredicate;
import com.google.auto.service.AutoService;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.AbstractFurnaceBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.RedstoneSide;

import java.util.HashMap;
import java.util.Map;

@AutoService(GameTestProvider.class)
public class BlockPredicateTests implements GameTestProvider {

    private static final BlockPos POS_1 = new BlockPos(1, 2, 1);
    private static final BlockPos POS_2 = POS_1.above();
    private static final BlockPos POS_3 = POS_2.above();

    @GameTest
    public void anyMatch(GameTestHelper helper) {
        furnacePropertySetup(helper);

        if (!(helper.getBlockEntity(POS_1) instanceof AbstractFurnaceBlockEntity furnace)) {
            helper.fail("BlockEntity at POS_1 is not a furnace!");
            return;
        }
        furnace.setItem(0, new ItemStack(Items.COAL, 1));

        var redstoneWireState = Blocks.REDSTONE_WIRE.defaultBlockState()
            .setValue(RedStoneWireBlock.EAST, RedstoneSide.SIDE)
            .setValue(RedStoneWireBlock.WEST, RedstoneSide.SIDE)
            .setValue(RedStoneWireBlock.POWER, 15);
        helper.setBlock(POS_2, redstoneWireState);

        BlockPredicate predicate = BlockPredicate.ANY;

        helper.succeedIf(() -> {
            if (!predicate.test(helper.getBlockState(POS_1))) {
                helper.fail("BlockPredicate failed to match block 1 that should have matched!");
            }
            if (!predicate.test(helper.getBlockState(POS_2))) {
                helper.fail("BlockPredicate failed to match block 2 that should have matched!");
            }
        });
    }

    @GameTest
    public void simpleBlock(GameTestHelper helper) {
        helper.setBlock(POS_1, Blocks.GLASS);

        BlockPredicate succeedPredicate = BlockPredicate.blocks(Blocks.GLASS).build();
        BlockPredicate failPredicate = BlockPredicate.blocks(Blocks.DIRT).build();

        helper.succeedIf(() -> {
            if (!succeedPredicate.test(helper.getBlockState(POS_1))) {
                helper.fail("BlockPredicate failed to match a block that should have matched!");
            }
            if (failPredicate.test(helper.getBlockState(POS_1))) {
                helper.fail("BlockPredicate matched a block that should not have matched!");
            }
        });
    }

    @GameTest
    public void multipleBlocks(GameTestHelper helper) {
        helper.setBlock(POS_1, Blocks.GLASS);
        helper.setBlock(POS_2, Blocks.DIRT);
        helper.setBlock(POS_3, Blocks.IRON_BLOCK);

        BlockPredicate predicate = BlockPredicate.blocks(Blocks.GLASS, Blocks.DIRT, Blocks.IRON_BLOCK).build();

        helper.succeedIf(() -> {
            if (!predicate.test(helper.getBlockState(POS_1))) {
                helper.fail("BlockPredicate failed to match block 1 that should have matched!");
            }
            if (!predicate.test(helper.getBlockState(POS_2))) {
                helper.fail("BlockPredicate failed to match block 2 that should have matched!");
            }
            if (!predicate.test(helper.getBlockState(POS_3))) {
                helper.fail("BlockPredicate failed to match block 3 that should have matched!");
            }
        });
    }

    @GameTest
    public void blockTag(GameTestHelper helper) {
        helper.setBlock(POS_1, Blocks.OAK_LOG);
        helper.setBlock(POS_2, Blocks.BIRCH_LOG);

        BlockPredicate predicate = BlockPredicate.tags(BlockTags.LOGS).build();

        helper.succeedIf(() -> {
            if (!predicate.test(helper.getBlockState(POS_1))) {
                helper.fail("BlockPredicate failed to match block 1 that should have matched!");
            }
            if (!predicate.test(helper.getBlockState(POS_2))) {
                helper.fail("BlockPredicate failed to match block 2 that should have matched!");
            }
        });
    }

    @GameTest
    public void subProperties(GameTestHelper helper) {
        furnacePropertySetup(helper);

        BlockPredicate predicate = BlockPredicate.blocks(Blocks.FURNACE)
            .hasProperty(AbstractFurnaceBlock.LIT, true)
            .build();

        helper.succeedIf(() -> {
            if (!predicate.test(helper.getBlockState(POS_1))) {
                helper.fail("BlockPredicate failed to match a block that should have matched!");
            }
        });
    }

    @GameTest
    public void fullProperties(GameTestHelper helper) {
        furnacePropertySetup(helper);

        BlockPredicate predicate = BlockPredicate.blocks(Blocks.FURNACE)
            .hasProperty(AbstractFurnaceBlock.LIT, true)
            .hasProperty(AbstractFurnaceBlock.FACING, Direction.EAST)
            .build();

        helper.succeedIf(() -> {
            if (!predicate.test(helper.getBlockState(POS_1))) {
                helper.fail("BlockPredicate failed to match a block that should have matched!");
            }
        });
    }

    @GameTest
    public void tagAndBlockProperties(GameTestHelper helper) {
        BlockState chestState = Blocks.CHEST.defaultBlockState()
            .setValue(ChestBlock.WATERLOGGED, true)
            .setValue(AbstractFurnaceBlock.FACING, Direction.EAST);
        helper.setBlock(POS_1, chestState);

        BlockState signState = Blocks.OAK_SIGN.defaultBlockState()
            .setValue(SignBlock.WATERLOGGED, true);
        helper.setBlock(POS_2, signState);

        BlockPredicate predicate = BlockPredicate.tags(BlockTags.SIGNS)
            .block(Blocks.CHEST)
            .hasProperty(SignBlock.WATERLOGGED, true)
            .build();

        helper.succeedIf(() -> {
            if (!predicate.test(helper.getBlockState(POS_1))) {
                helper.fail("BlockPredicate failed to match block 1 that should have matched!");
            }
            if (!predicate.test(helper.getBlockState(POS_2))) {
                helper.fail("BlockPredicate failed to match block 2 that should have matched!");
            }
        });
    }

    @GameTest
    public void propertyDifferences(GameTestHelper helper) {
        BlockState furnaceState = furnacePropertySetup(helper);

        BlockPredicate predicate = BlockPredicate.blocks(Blocks.FURNACE)
            .hasProperty(AbstractFurnaceBlock.LIT, false)
            .hasProperty(AbstractFurnaceBlock.FACING, Direction.WEST)
            .build();

        Map<String, String> differences = new HashMap<>();
        predicate.difference(furnaceState, (property, difference) -> {
            differences.put(difference.getName(), difference.getValueRepresentation());
        });

        helper.succeedIf(() -> {
            if (!differences.toString().equals("{lit=false, facing=west}")) {
                helper.fail("BlockPredicate differences did not match expected: " + differences);
            }
        });
    }

    private BlockState furnacePropertySetup(GameTestHelper helper) {
        BlockState furnaceState = Blocks.FURNACE.defaultBlockState()
            .setValue(AbstractFurnaceBlock.LIT, true)
            .setValue(AbstractFurnaceBlock.FACING, Direction.EAST);
        helper.setBlock(POS_1, furnaceState);

        return furnaceState;
    }
}
