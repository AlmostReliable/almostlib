package com.almostreliable.almostlib.fabric;

import com.almostreliable.almostlib.AlmostLib;
import com.almostreliable.almostlib.component.ComponentHolder;
import com.almostreliable.almostlib.fabric.compat.energy.RebornEnergyCompat;
import com.almostreliable.almostlib.gametest.GameTestLoader;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.transfer.v1.item.ItemStorage;
import net.fabricmc.fabric.impl.transfer.item.InventoryStorageImpl;

public class FabricInitializer implements ModInitializer {

    @Override
    public void onInitialize() {
        AlmostLib.initNetworkHandler();
        applyBlockEntityLookups();

        var gametests = System.getProperty("fabric-api.gametest");
        if ("1".equals(gametests) || "true".equals(gametests)) {
            GameTestLoader.load();
        }
    }

    @SuppressWarnings("UnstableApiUsage")
    private void applyBlockEntityLookups() {
        ItemStorage.SIDED.registerFallback(
            (world, pos, state, blockEntity, direction) -> {
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
