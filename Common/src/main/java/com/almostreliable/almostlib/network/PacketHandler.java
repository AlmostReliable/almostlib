package com.almostreliable.almostlib.network;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;

public interface PacketHandler<T> {

    void encode(T packet, FriendlyByteBuf buffer);

    T decode(FriendlyByteBuf buffer);

    ResourceLocation getChannelId();

    Class<T> getPacketType();

    interface C2S<T> extends PacketHandler<T> {

        void handle(T packet, ServerPlayer player);
    }

    interface S2C<T> extends PacketHandler<T> {

        @Environment(EnvType.CLIENT)
        void handle(T packet);
    }
}
