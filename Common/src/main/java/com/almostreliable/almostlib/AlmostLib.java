package com.almostreliable.almostlib;

import com.almostreliable.almostlib.network.NetworkHandler;
import com.almostreliable.almostlib.network.PacketBus;
import com.almostreliable.almostlib.network.synchronizer.MenuSyncPacket;
import net.minecraft.resources.ResourceLocation;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.ApiStatus;

import java.util.ServiceLoader;

public class AlmostLib {

    public static final Logger LOGGER = LogManager.getLogger(BuildConfig.MOD_NAME);
    public static final AlmostLibPlatform PLATFORM = loadService(AlmostLibPlatform.class);

    private static final NetworkHandler NETWORK_HANDLER = NetworkHandler.create(BuildConfig.MOD_ID, "simple");
    public static final PacketBus.S2C<MenuSyncPacket> SYNC_PACKET = NETWORK_HANDLER.S2C(new MenuSyncPacket());

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
}
