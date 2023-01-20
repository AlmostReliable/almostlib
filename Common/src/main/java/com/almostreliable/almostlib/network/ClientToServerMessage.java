package com.almostreliable.almostlib.network;

import net.minecraft.server.level.ServerPlayer;

public interface ClientToServerMessage<T> {

    void handle(T packet, ServerPlayer player);
}
