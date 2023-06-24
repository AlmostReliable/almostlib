package testmod;

import com.almostreliable.almostlib.AlmostManager;
import com.almostreliable.almostlib.item.AlmostCreativeTab;
import com.almostreliable.almostlib.registry.BlockEntityEntry;
import com.almostreliable.almostlib.registry.BlockEntry;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import testmod.block.TestEnergyGeneratorBlock;

public class TestMod {

    public static final AlmostManager MANAGER = AlmostManager.create("testmod")
        .defaultCreativeTab(new AlmostCreativeTab("testmod", "Testmod") {
            @Override
            public ItemStack makeIcon() {
                return Items.ACACIA_LOG.getDefaultInstance();
            }
        });

    public static final BlockEntry<TestEnergyGeneratorBlock> ENERGY_GENERATOR = MANAGER.blocks()
        .builder("energy_generator", TestEnergyGeneratorBlock::new)
        .register();

    public static final BlockEntityEntry<TestEnergyGeneratorBlock.Entity> ENERGY_GENERATOR_ENTITY = MANAGER.blockEntities()
        .builder("energy_generator", TestEnergyGeneratorBlock.Entity::new)
        .block(ENERGY_GENERATOR)
        .register();

    public static void init() {
        MANAGER.initRegistriesToLoader();
    }
}
