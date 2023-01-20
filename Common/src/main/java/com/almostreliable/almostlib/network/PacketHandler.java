package com.almostreliable.almostlib.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;

public interface PacketHandler<T> {

    ResourceLocation getChannelId();

    Class<T> getPacketType();

    void encode(T packet, FriendlyByteBuf buffer);

    T decode(FriendlyByteBuf buffer);

    interface C2S<T> extends PacketHandler<T> {

        void handle(T packet, ServerPlayer player);
    }

    interface S2C<T> extends PacketHandler<T> {

        void handle(T packet);
    }
}
