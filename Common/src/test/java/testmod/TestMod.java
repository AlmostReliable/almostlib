package testmod;

import com.almostreliable.lib.AlmostLib;
import com.almostreliable.lib.AlmostManager;
import com.almostreliable.lib.config.Config;
import com.almostreliable.lib.config.ConfigManager;
import com.almostreliable.lib.gametest.GameTestLoader;
import com.almostreliable.lib.item.AlmostCreativeTab;
import com.almostreliable.lib.registry.BlockEntityEntry;
import com.almostreliable.lib.registry.BlockEntry;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import testmod.block.EnergyBatteryBlock;
import testmod.block.EnergyGeneratorBlock;
import testmod.block.StorageBlock;
import testmod.gametest.BlockPredicateTests;
import testmod.gametest.EnergyContainerTests;
import testmod.gametest.ItemContainerTests;

public final class TestMod {

    static {
        ConfigManager.registerServerReloadable("testmod.toml", ExampleConfig.class, ExampleConfig::new);
    }

    public static final Config<ExampleConfig> CONFIG = ConfigManager.get(ExampleConfig.class);

    public static final AlmostManager MANAGER = AlmostManager.create("testmod")
        .defaultCreativeTab(new AlmostCreativeTab("testmod", "Testmod") {
            @Override
            public ItemStack makeIcon() {
                return Items.ACACIA_LOG.getDefaultInstance();
            }
        });

    public static final BlockEntry<StorageBlock> STORAGE_BLOCK = MANAGER.blocks()
        .builder("storage_block", StorageBlock::new)
        .register();

    public static final BlockEntityEntry<StorageBlock.Entity> STORAGE_ENTITY = MANAGER.blockEntities()
        .builder("storage_block", StorageBlock.Entity::new)
        .block(STORAGE_BLOCK)
        .register();

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
            GameTestLoader.registerProviders(
                BlockPredicateTests.class,
                EnergyContainerTests.class,
                ItemContainerTests.class
            );
        }
    }
}
