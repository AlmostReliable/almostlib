package testmod;

import com.almostreliable.almostlib.AlmostLib;
import com.almostreliable.almostlib.AlmostManager;
import com.almostreliable.almostlib.gametest.GameTestLoader;
import com.almostreliable.almostlib.item.AlmostCreativeTab;
import com.almostreliable.almostlib.registry.BlockEntityEntry;
import com.almostreliable.almostlib.registry.BlockEntry;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import testmod.block.EnergyBatteryBlock;
import testmod.block.EnergyGeneratorBlock;
import testmod.gametest.BlockPredicateTests;
import testmod.gametest.EnergyComponentTests;

public final class TestMod {

    public static final AlmostManager MANAGER = AlmostManager.create("testmod")
        .defaultCreativeTab(new AlmostCreativeTab("testmod", "Testmod") {
            @Override
            public ItemStack makeIcon() {
                return Items.ACACIA_LOG.getDefaultInstance();
            }
        });

    public static final BlockEntry<EnergyGeneratorBlock> ENERGY_GENERATOR = MANAGER.blocks()
        .builder("energy_generator", EnergyGeneratorBlock::new)
        .register();

    public static final BlockEntityEntry<EnergyGeneratorBlock.Entity> ENERGY_GENERATOR_ENTITY = MANAGER.blockEntities()
        .builder("energy_generator", EnergyGeneratorBlock.Entity::new)
        .block(ENERGY_GENERATOR)
        .register();

    public static final BlockEntry<EnergyBatteryBlock> ENERGY_BATTERY = MANAGER.blocks()
        .builder("energy_battery", EnergyBatteryBlock::new)
        .register();

    public static final BlockEntityEntry<EnergyBatteryBlock.Entity> ENERGY_BATTERY_ENTITY = MANAGER.blockEntities()
        .builder("energy_battery", EnergyBatteryBlock.Entity::new)
        .block(ENERGY_BATTERY)
        .register();

    private TestMod() {}

    public static void init() {
        MANAGER.initRegistriesToLoader();
        if (AlmostLib.PLATFORM.isGameTestEnabled()) {
            GameTestLoader.registerProviders(BlockPredicateTests.class, EnergyComponentTests.class);
        }
    }
}
