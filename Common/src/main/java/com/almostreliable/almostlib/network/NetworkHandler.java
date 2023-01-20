package com.almostreliable.almostlib.network;

import com.almostreliable.almostlib.AlmostLib;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;

import java.util.ArrayList;
import java.util.List;

/**
 * A class that handles the registration of packets and the sending of packets.
 * <p>
 * Usage:
 * <pre> {@code
 * NetworkHandler NETWORK_HANDLER = NetworkHandler.create("my_mod", "simple_channels");
 * PacketBus.C2S<T> SERVER_PACKET_BUS = NETWORK_HANDLER.C2S(PacketHandler<T>);
 * PacketBus.S2C<T> CLIENT_PACKET_BUS = NETWORK_HANDLER.S2C(PacketHandler<T>);
 * }</pre>
 * From there you can send packets using the created {@link PacketBus}.
 * <p>
 * Make sure to initialize the NetworkHandler correctly.<br>
 * - Fabric: Call  {@link #init()} in your default mod initializer method<br>
 * - Forge: Call {@link #init()} in the FMLCommonSetupEvent
 */
public abstract class NetworkHandler {

    protected final List<PacketHandler.C2S<?>> C2SPacketHandlers = new ArrayList<>();
    protected final List<PacketHandler.S2C<?>> S2CPacketHandlers = new ArrayList<>();
    protected final ResourceLocation id;
    private boolean isFrozen = false;

    public NetworkHandler(ResourceLocation id) {
        this.id = id;
    }

    public static NetworkHandler create(String namespace, String handlerId) {
        return AlmostLib.PLATFORM.createNetworkHandler(new ResourceLocation(namespace, handlerId));
    }

    public final void init() {
        this.isFrozen = true;
        this.initPackets();
    }

    protected abstract void initPackets();

    /**
     * Creates a client to server packet bus for the given packet handler.
     *
     * @param handler The packet handler to register.
     * @param <T>     The packet type
     * @return The packet bus
     */
    public final <T> PacketBus.C2S<T> C2S(PacketHandler.C2S<T> handler) {
        if (this.isFrozen) {
            throw new IllegalStateException("Cannot register C2S packet after init");
        }

        if (C2SPacketHandlers.contains(handler)) {
            throw new IllegalArgumentException("Handler " + handler.getChannelId() + " already registered for C2S");
        }

        var bus = new PacketBus.C2S<>(handler, this);
        C2SPacketHandlers.add(handler);
        return bus;
    }

    /**
     * Creates a server to client packet bus for the given packet handler.
     *
     * @param handler The packet handler to register.
     * @param <T>     The packet type
     * @return The packet bus
     */
    public final <T> PacketBus.S2C<T> S2C(PacketHandler.S2C<T> handler) {
        if (this.isFrozen) {
            throw new IllegalStateException("Cannot register S2C packet after init");
        }

        if (S2CPacketHandlers.contains(handler)) {
            throw new IllegalArgumentException("Handler " + handler.getChannelId() + " already registered for S2C");
        }

        var bus = new PacketBus.S2C<>(handler, this);
        S2CPacketHandlers.add(handler);
        return bus;
    }

    protected abstract <T> void sendToServer(PacketHandler<T> handler, T packet);

    protected abstract <T> void sendToClient(ServerPlayer player, PacketHandler<T> handler, T packet);

    protected abstract <T> void broadcast(ServerLevel level, PacketHandler<T> handler, T packet);

    protected abstract <T> void broadcastChunk(ServerLevel level, BlockPos pos, PacketHandler<T> handler, T packet);
}
