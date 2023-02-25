package com.almostreliable.almostlib.menu.synchronizer;

import com.almostreliable.almostlib.AlmostLib;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;

public interface ClientAction {

    static void sendAction(AbstractContainerScreen<?> screen, CompoundTag nbt) {
        sendAction(screen.getMenu().containerId, nbt);
    }

    static void sendAction(int containerId, CompoundTag nbt) {
        ClientActionHandler.Packet packet = new ClientActionHandler.Packet(containerId, nbt);
        AlmostLib.CLIENT_ACTION_HANDLER.send(packet);
    }

    interface Receiver {

        void onReceiveAction(ServerPlayer player, CompoundTag nbt);
    }
}
