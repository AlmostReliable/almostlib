package com.almostreliable.lib.forge.network;

import com.almostreliable.lib.network.NetworkHandler;
import com.almostreliable.lib.network.PacketHandler;
import com.almostreliable.lib.util.AlmostUtils;
import com.google.common.base.Preconditions;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.simple.SimpleChannel;
import org.jetbrains.annotations.ApiStatus;

@ApiStatus.Internal
public class NetworkHandlerForge extends NetworkHandler {

    private static final String PROTOCOL_VERSION = "1";
    private final SimpleChannel channel;

    public NetworkHandlerForge(ResourceLocation id) {
        super(id);
        channel = NetworkRegistry.ChannelBuilder.named(id)
            .clientAcceptedVersions(PROTOCOL_VERSION::equals)
            .serverAcceptedVersions(PROTOCOL_VERSION::equals)
            .networkProtocolVersion(() -> PROTOCOL_VERSION)
            .simpleChannel();
    }

    @Override
    protected void initPackets() {
        int i = 0;

        for (PacketHandler.S2C<?> packetHandler : S2CPacketHandlers) {
            PacketHandler.S2C<Object> plsHelpMe = AlmostUtils.cast(packetHandler);
            channel.registerMessage(i++, plsHelpMe.getPacketType(), plsHelpMe::encode, plsHelpMe::decode, (packet, ctx) -> {
                NetworkEvent.Context context = ctx.get();
                Minecraft.getInstance().execute(() -> {
                    plsHelpMe.handle(packet);
                });
                context.setPacketHandled(true);
            });
        }

        for (PacketHandler.C2S<?> packetHandler : C2SPacketHandlers) {
            PacketHandler.C2S<Object> plsHelpMe = AlmostUtils.cast(packetHandler);
            channel.registerMessage(i++, plsHelpMe.getPacketType(), plsHelpMe::encode, plsHelpMe::decode, (packet, ctx) -> {
                NetworkEvent.Context context = ctx.get();
                ServerPlayer player = context.getSender();
                if (player == null) {
                    context.setPacketHandled(true);
                    return;
                }

                MinecraftServer server = player.getServer();

                if (server == null) {
                    context.setPacketHandled(true);
                    return;
                }

                server.execute(() -> {
                    plsHelpMe.handle(packet, player);
                });

                context.setPacketHandled(true);
            });
        }
    }

    @Override
    protected <T> void sendToServer(PacketHandler<T> handler, T packet) {
        assertPacketMismatch(handler, packet);
        channel.sendToServer(packet);
    }

    @Override
    protected <T> void sendToClient(ServerPlayer player, PacketHandler<T> handler, T packet) {
        assertPacketMismatch(handler, packet);
        channel.send(PacketDistributor.PLAYER.with(() -> player), packet);
    }

    @Override
    protected <T> void broadcast(ServerLevel level, PacketHandler<T> handler, T packet) {
        assertPacketMismatch(handler, packet);
        channel.send(PacketDistributor.ALL.noArg(), packet);
    }

    @Override
    protected <T> void broadcastChunk(ServerLevel level, BlockPos pos, PacketHandler<T> handler, T packet) {
        assertPacketMismatch(handler, packet);
        channel.send(PacketDistributor.TRACKING_CHUNK.with(() -> level.getChunkAt(pos)), packet);
    }

    private <T> void assertPacketMismatch(PacketHandler<T> handler, T packet) {
        Preconditions.checkArgument(handler.getPacketType().isInstance(packet), "Packet type mismatch");
    }
}
