package com.github.almostreliable.testmod;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;

public class TestModFabric implements ModInitializer, ClientModInitializer, DataGeneratorEntrypoint {
    @Override
    public void onInitialize() {
        TestModCommon.MANAGER.registerManager();
    }

    @Override
    public void onInitializeClient() {
        TestModCommon.MANAGER.registerClientManager();
    }

    @Override
    public void onInitializeDataGenerator(FabricDataGenerator dataGenerator) {

    }
}
