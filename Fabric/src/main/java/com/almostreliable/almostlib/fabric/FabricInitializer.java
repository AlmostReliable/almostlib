package com.almostreliable.almostlib.fabric;

import com.almostreliable.almostlib.AlmostLib;
import com.almostreliable.almostlib.component.ComponentHolder;
import com.almostreliable.almostlib.fabric.component.compat.RebornEnergyCompat;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.transfer.v1.item.ItemStorage;
import net.fabricmc.fabric.impl.transfer.item.InventoryStorageImpl;

public class FabricInitializer implements ModInitializer {

    @Override
    public void onInitialize() {
        AlmostLib.initNetworkHandler();
        attachComponentApi();
    }

    @SuppressWarnings("UnstableApiUsage")
    private void attachComponentApi() {
        ItemStorage.SIDED.registerFallback(
            (level, pos, state, blockEntity, direction) -> {
                if (blockEntity instanceof ComponentHolder componentHolder) {
                    var container = componentHolder.getItemContainer(direction);
                    if (container != null) {
                        return InventoryStorageImpl.of(container, direction);
                    }
                }

                return null;
            }
        );

        RebornEnergyCompat.INSTANCE.registerEnergyStorage();
    }
}
