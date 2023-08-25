package com.almostreliable.lib.menu;

import com.almostreliable.lib.AlmostLib;
import com.almostreliable.lib.menu.network.synchronizer.MenuSyncHandler;
import com.almostreliable.lib.menu.network.synchronizer.MenuSynchronizer;
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
    private final Inventory inventory;

    protected SynchronizedContainerMenu(MenuType<?> menuType, int wid, Inventory inventory, T blockEntity) {
        super(menuType, wid);
        this.blockEntity = blockEntity;
        this.sync = new MenuSynchronizer();
        this.inventory = inventory;
    }

    @Override
    public void sendAllDataToRemote() {
        super.sendAllDataToRemote();
        if (sync.hasDataHandlers()) {
            AlmostLib.SYNC_PACKET.send((ServerPlayer) inventory.player, MenuSyncHandler.of(containerId, sync::encodeAll));
        }
    }

    @Override
    public void broadcastChanges() {
        if (!inventory.player.level.isClientSide && sync.hasChanged()) {
            AlmostLib.SYNC_PACKET.send((ServerPlayer) inventory.player, MenuSyncHandler.of(containerId, sync::encode));
        }
        super.broadcastChanges();
    }

    @Override
    public boolean stillValid(Player player) {
        return blockEntity.getLevel() != null && stillValid(
            ContainerLevelAccess.create(blockEntity.getLevel(), blockEntity.getBlockPos()),
            player,
            blockEntity.getBlockState().getBlock()
        );
    }

    @ApiStatus.Internal
    public void receiveServerData(FriendlyByteBuf data) {
        sync.decode(data);
    }
}
