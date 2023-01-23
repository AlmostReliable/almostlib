package com.almostreliable.almostlib.network.synchronizer;

import com.almostreliable.almostlib.AlmostLib;
import com.almostreliable.almostlib.menu.AlmostContainerMenu;
import com.almostreliable.almostlib.network.PacketHandler;
import io.netty.buffer.Unpooled;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.ApiStatus;

import java.util.function.Consumer;

@ApiStatus.Internal
public class MenuSyncPacket implements PacketHandler.S2C<MenuSyncPacket> {

    private int menuId;
    private FriendlyByteBuf data;

    public MenuSyncPacket(int menuId, Consumer<FriendlyByteBuf> writer) {
        this.menuId = menuId;
        this.data = new FriendlyByteBuf(Unpooled.buffer());
        writer.accept(data);
    }

    public MenuSyncPacket() {}

    @Override
    public void handle(MenuSyncPacket packet) {
        var player = Minecraft.getInstance().player;
        if (player == null && player.containerMenu instanceof AlmostContainerMenu<?> menu && packet.menuId == menu.containerId) {
            menu.receiveServerData(packet.data);
        }
    }

    @Override
    public ResourceLocation getChannelId() {
        return AlmostLib.getRL("menu_sync");
    }

    @Override
    public Class<MenuSyncPacket> getPacketType() {
        return MenuSyncPacket.class;
    }

    @Override
    public void encode(MenuSyncPacket packet, FriendlyByteBuf buffer) {
        buffer.writeInt(packet.menuId);
        buffer.writeInt(packet.data.readableBytes());
        buffer.writeBytes(packet.data, packet.data.readableBytes());
    }

    @Override
    public MenuSyncPacket decode(FriendlyByteBuf buffer) {
        var packet = new MenuSyncPacket();
        packet.menuId = buffer.readInt();
        var length = buffer.readInt();
        packet.data = new FriendlyByteBuf(buffer.readBytes(length));
        return packet;
    }
}
