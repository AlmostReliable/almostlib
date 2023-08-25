package com.almostreliable.lib.fabric.network;

import com.almostreliable.lib.AlmostLib;
import com.almostreliable.lib.network.NetworkHandler;
import com.almostreliable.lib.network.PacketHandler;
import com.almostreliable.lib.util.AlmostUtils;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.ChunkPos;
import org.jetbrains.annotations.ApiStatus;

import java.util.List;

@ApiStatus.Internal
public class NetworkHandlerFabric extends NetworkHandler {

    public NetworkHandlerFabric(ResourceLocation id) {
        super(id);
    }

    @Override
    public void initPackets() {
        if (AlmostLib.PLATFORM.isClient()) {
            initPacketsClient();
        }
        initPacketsServer();
    }

    private void initPacketsServer() {
        for (PacketHandler.C2S<?> packetHandler : C2SPacketHandlers) {
            ServerPlayNetworking.PlayChannelHandler fabricHandler = (server, player, handler, buffer, responseSender) -> {
                var packet = packetHandler.decode(buffer);
                server.execute(() -> packetHandler.handle(AlmostUtils.cast(packet), player));
            };
            ServerPlayNetworking.registerGlobalReceiver(packetHandler.getChannelId(), fabricHandler);
        }
    }

    @Environment(EnvType.CLIENT)
    private void initPacketsClient() {
        for (PacketHandler.S2C<?> packetHandler : S2CPacketHandlers) {
            ClientPlayNetworking.PlayChannelHandler fabricHandler = (client, listener, buffer, response) -> {
                var packet = packetHandler.decode(buffer);
                client.execute(() -> packetHandler.handle(AlmostUtils.cast(packet)));
            };
            ClientPlayNetworking.registerGlobalReceiver(packetHandler.getChannelId(), fabricHandler);
        }
    }

    @Override
    protected <T> void sendToServer(PacketHandler<T> handler, T packet) {
        var buffer = PacketByteBufs.create();
        handler.encode(packet, buffer);
        ClientPlayNetworking.send(handler.getChannelId(), buffer);
    }

    @Override
    protected <T> void sendToClient(ServerPlayer player, PacketHandler<T> handler, T packet) {
        var buffer = PacketByteBufs.create();
        handler.encode(packet, buffer);
        ServerPlayNetworking.send(player, handler.getChannelId(), buffer);
    }

    @Override
    protected <T> void broadcast(ServerLevel level, PacketHandler<T> handler, T packet) {
        for (ServerPlayer player : level.getServer().getPlayerList().getPlayers()) {
            sendToClient(player, handler, packet);
        }
    }

    @Override
    protected <T> void broadcastChunk(ServerLevel level, BlockPos pos, PacketHandler<T> handler, T packet) {
        List<ServerPlayer> players = level.getChunkSource().chunkMap.getPlayers(new ChunkPos(pos), false);
        for (ServerPlayer player : players) {
            sendToClient(player, handler, packet);
        }
    }
}
