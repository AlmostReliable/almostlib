package com.almostreliable.almostlib.menu;

import com.almostreliable.almostlib.AlmostLib;
import com.almostreliable.almostlib.menu.synchronizer.MenuSyncPacket;
import com.almostreliable.almostlib.menu.synchronizer.MenuSynchronizer;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.jetbrains.annotations.ApiStatus;

/**
 * An {@link AbstractContainerMenu} with a {@link MenuSynchronizer} and a linked {@link BlockEntity}.
 * <p>
 * The {@link MenuSynchronizer} can be used to synchronize data between the server and the client.
 * It automatically sends the data to the client when the menu is opened and updates the client when
 * the data changes.
 * <p>
 * Usage:
 * <pre> {@code
 * sync.addDataHandler(DataHandler)
 * }</pre>
 * Example:
 * <pre> {@code
 * sync.addDataHandler(new IntegerDataHandler(blockEntity::getProgress, this::setProgress)
 * }</pre>
 *
 * @param <T> The type of the linked {@link BlockEntity}.
 */
public abstract class SynchronizedContainerMenu<T extends BlockEntity> extends AbstractContainerMenu {

    public final T blockEntity;
    protected final MenuSynchronizer sync;
    private final Inventory playerInventory;

    protected SynchronizedContainerMenu(MenuType<?> menuType, int windowId, Inventory playerInventory, T blockEntity) {
        super(menuType, windowId);
        this.blockEntity = blockEntity;
        this.sync = new MenuSynchronizer();
        this.playerInventory = playerInventory;
    }

    @Override
    public boolean stillValid(Player player) {
        return blockEntity.getLevel() != null && stillValid(
            ContainerLevelAccess.create(blockEntity.getLevel(), blockEntity.getBlockPos()),
            player,
            blockEntity.getBlockState().getBlock()
        );
    }

    @Override
    public void sendAllDataToRemote() {
        super.sendAllDataToRemote();
        if (sync.hasDataHandlers()) {
            AlmostLib.SYNC_PACKET.send((ServerPlayer) playerInventory.player, new MenuSyncPacket(containerId, sync::encodeAll));
        }
    }

    @Override
    public void broadcastChanges() {
        if (!playerInventory.player.level.isClientSide && sync.hasChanged()) {
            AlmostLib.SYNC_PACKET.send((ServerPlayer) playerInventory.player, new MenuSyncPacket(containerId, sync::encode));
        }
        super.broadcastChanges();
    }

    @ApiStatus.Internal
    public void receiveServerData(FriendlyByteBuf data) {
        sync.decode(data);
    }
}
