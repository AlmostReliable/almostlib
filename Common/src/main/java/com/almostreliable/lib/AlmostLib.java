package com.almostreliable.lib;

import com.almostreliable.lib.menu.network.ClientActionHandler;
import com.almostreliable.lib.menu.network.synchronizer.MenuSyncHandler;
import com.almostreliable.lib.network.NetworkHandler;
import com.almostreliable.lib.network.PacketBus;
import net.minecraft.resources.ResourceLocation;
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
}
