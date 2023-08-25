package com.almostreliable.lib.menu.network;

import com.almostreliable.lib.AlmostLib;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;

/**
 * Utility interface to send actions from the client to the server.<br>
 * Doesn't need to be implemented.
 */
public interface ClientAction {

    /**
     * Sends an action from the client to the server.
     * <p>
     * The receiving menu must implement {@link ClientAction.Receiver}.
     *
     * @param screen The screen to send the action from.
     * @param nbt    The action data.
     */
    static void sendAction(AbstractContainerScreen<?> screen, CompoundTag nbt) {
        sendAction(screen.getMenu().containerId, nbt);
    }

    /**
     * Sends an action from the client to the server.
     * <p>
     * The receiving menu must implement {@link ClientAction.Receiver}.
     *
     * @param containerId The container ID to send the action from.
     * @param nbt         The action data.
     */
    static void sendAction(int containerId, CompoundTag nbt) {
        ClientActionHandler.Packet packet = new ClientActionHandler.Packet(containerId, nbt);
        AlmostLib.CLIENT_ACTION_HANDLER.send(packet);
    }

    /**
     * Implemented by menus able to receive actions from the client.
     * <p>
     * To differentiate the action type, the {@link CompoundTag} should include an identifier.
     */
    interface Receiver {

        /**
         * Called when an action is received from the client.
         *
         * @param player The player who sent the action.
         * @param nbt    The action data.
         */
        void onReceiveAction(ServerPlayer player, CompoundTag nbt);
    }
}
