package com.github.almostreliable.testmod;

import com.github.almostreliable.lib.AlmostLib;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;

public class TestModFabric implements ModInitializer, ClientModInitializer, DataGeneratorEntrypoint {
    @Override
    public void onInitialize() {
        AlmostLib.LOG.info("dfd");
        TestModCommon.init();
    }

    @Override
    public void onInitializeClient() {

    }

    @Override
    public void onInitializeDataGenerator(FabricDataGenerator dataGenerator) {

    }
}
