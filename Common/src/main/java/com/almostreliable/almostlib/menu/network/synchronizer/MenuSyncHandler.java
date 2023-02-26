package com.almostreliable.almostlib.menu.network.synchronizer;

import com.almostreliable.almostlib.AlmostLib;
import com.almostreliable.almostlib.menu.SynchronizedContainerMenu;
import com.almostreliable.almostlib.network.PacketHandler;
import io.netty.buffer.Unpooled;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.ApiStatus;

import java.util.function.Consumer;

@ApiStatus.Internal
public class MenuSyncHandler implements PacketHandler.S2C<MenuSyncHandler.Packet> {

    private static final ResourceLocation CHANNEL_ID = AlmostLib.getRL("menu_sync");

    public static Packet of(int containerId, Consumer<FriendlyByteBuf> consumer) {
        var buffer = new FriendlyByteBuf(Unpooled.buffer());
        consumer.accept(buffer);
        return new Packet(containerId, buffer);
    }

    @Override
    public void handle(Packet packet) {
        var player = Minecraft.getInstance().player;
        if (player != null && player.containerMenu instanceof SynchronizedContainerMenu<?> menu &&
            packet.containerId == menu.containerId) {
            menu.receiveServerData(packet.data);
        }
    }

    @Override
    public ResourceLocation getChannelId() {
        return CHANNEL_ID;
    }

    @Override
    public Class<Packet> getPacketType() {
        return Packet.class;
    }

    @Override
    public void encode(Packet packet, FriendlyByteBuf buffer) {
        buffer.writeInt(packet.containerId);
        buffer.writeInt(packet.data.readableBytes());
        buffer.writeBytes(packet.data, packet.data.readableBytes());
    }

    @Override
    public Packet decode(FriendlyByteBuf buffer) {
        var containerId = buffer.readInt();
        var length = buffer.readInt();
        var data = new FriendlyByteBuf(buffer.readBytes(length));
        return new Packet(containerId, data);
    }

    public record Packet(int containerId, FriendlyByteBuf data) {

    }
}
