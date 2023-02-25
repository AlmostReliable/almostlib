package com.almostreliable.almostlib.menu.network;

import com.almostreliable.almostlib.AlmostLib;
import com.almostreliable.almostlib.network.PacketHandler;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import org.jetbrains.annotations.ApiStatus;

@ApiStatus.Internal
public class ClientActionHandler implements PacketHandler.C2S<ClientActionHandler.Packet> {

    private static final ResourceLocation CHANNEL_ID = AlmostLib.getRL("client_action");

    @Override
    public ResourceLocation getChannelId() {
        return CHANNEL_ID;
    }

    @Override
    public Class<Packet> getPacketType() {
        return Packet.class;
    }

    @Override
    public Packet decode(FriendlyByteBuf buffer) {
        int containerId = buffer.readInt();
        CompoundTag nbt = buffer.readNbt();
        if (nbt == null) {
            nbt = new CompoundTag();
        }
        return new Packet(containerId, nbt);
    }

    @Override
    public void encode(Packet packet, FriendlyByteBuf buffer) {
        buffer.writeInt(packet.containerId());
        buffer.writeNbt(packet.nbt());
    }

    @Override
    public void handle(Packet packet, ServerPlayer player) {
        if (player.containerMenu.containerId != packet.containerId()) {
            return;
        }

        if (player.containerMenu instanceof ClientAction.Receiver receiver) {
            receiver.onReceiveAction(player, packet.nbt());
        }
    }


    public record Packet(int containerId, CompoundTag nbt) {

    }
}
