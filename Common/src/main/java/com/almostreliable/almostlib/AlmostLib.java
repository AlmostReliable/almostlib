package com.almostreliable.almostlib;

import com.almostreliable.almostlib.menu.network.ClientActionHandler;
import com.almostreliable.almostlib.menu.network.synchronizer.MenuSyncHandler;
import com.almostreliable.almostlib.network.NetworkHandler;
import com.almostreliable.almostlib.network.PacketBus;
import com.almostreliable.almostlib.registry.BlockEntityEntry;
import com.almostreliable.almostlib.registry.BlockEntry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.level.material.Material;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.ApiStatus;

import java.util.ServiceLoader;

public class AlmostLib {

    public static final Logger LOGGER = LogManager.getLogger(BuildConfig.MOD_NAME);
    public static final AlmostLibPlatform PLATFORM = loadService(AlmostLibPlatform.class);

    private static final NetworkHandler NETWORK_HANDLER = NetworkHandler.create(BuildConfig.MOD_ID, "simple");
    public static final PacketBus.S2C<MenuSyncHandler.Packet> SYNC_PACKET = NETWORK_HANDLER.S2C(new MenuSyncHandler());
    public static final PacketBus.C2S<ClientActionHandler.Packet> CLIENT_ACTION_HANDLER = NETWORK_HANDLER.C2S(new ClientActionHandler());

    @ApiStatus.Internal
    public static ResourceLocation getRL(String path) {
        return new ResourceLocation(BuildConfig.MOD_ID, path);
    }

    public static <T> T loadService(Class<T> clazz) {
        return ServiceLoader
            .load(clazz)
            .findFirst()
            .orElseThrow(() -> new NullPointerException("Failed to load service for class: " + clazz.getName()));
    }

    public static void initNetworkHandler() {
        NETWORK_HANDLER.init();
    }

    public static AlmostManager AM = AlmostManager.create("almostlib").defaultCreativeTab(CreativeModeTab.TAB_COMBAT);

    public static BlockEntry<CoalGenerator.Blog> COAL_BLOCK = AM.blocks()
        .builder("coal_foo", Material.CACTUS, CoalGenerator.Blog::new)
        .defaultItem()
        .register();

    public static BlockEntityEntry<CoalGenerator> COAL_BE = AM.blockEntities()
        .builder("coal_foo", CoalGenerator::new)
        .block(COAL_BLOCK)
        .register();
}
