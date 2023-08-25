package com.almostreliable.lib.network;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;

public abstract class PacketBus<T> {

    protected final PacketHandler<T> handler;
    protected final NetworkHandler networkHandler;

    protected PacketBus(PacketHandler<T> handler, NetworkHandler networkHandler) {
        this.handler = handler;
        this.networkHandler = networkHandler;
    }

    public static class C2S<T> extends PacketBus<T> {

        public C2S(PacketHandler.C2S<T> handler, NetworkHandler networkHandler) {
            super(handler, networkHandler);
        }

        /**
         * Sends a packet to the server.
         *
         * @param packet The packet to send.
         */
        public void send(T packet) {
            this.networkHandler.sendToServer(handler, packet);
        }
    }

    public static class S2C<T> extends PacketBus<T> {

        public S2C(PacketHandler.S2C<T> handler, NetworkHandler networkHandler) {
            super(handler, networkHandler);
        }

        /**
         * Send a packet to a specific player
         *
         * @param player The player to send the packet to
         * @param packet The packet to send
         */
        public void send(ServerPlayer player, T packet) {
            this.networkHandler.sendToClient(player, this.handler, packet);
        }

        /**
         * Broadcasts the packet to all players in the given level.
         *
         * @param level  The level to broadcast to.
         * @param packet The packet to broadcast.
         */
        public void broadcast(ServerLevel level, T packet) {
            this.networkHandler.broadcast(level, this.handler, packet);
        }

        /**
         * Broadcasts the packet to all players tracking the given chunk at a block position.
         *
         * @param level  The level the chunk is in.
         * @param pos    The position of the block in the chunk.
         * @param packet The packet to send.
         */
        public void broadcastChunk(ServerLevel level, BlockPos pos, T packet) {
            this.networkHandler.broadcastChunk(level, pos, this.handler, packet);
        }
    }
}
