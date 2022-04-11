package com.github.almostreliable.lib;

import com.github.almostreliable.lib.registry.RegistryManager;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.entrypoint.EntrypointContainer;

import java.util.List;

public class AlmostLibFabric implements ModInitializer, ClientModInitializer, DataGeneratorEntrypoint {
    private static final String INIT_KEY = "almostlib-init";

    @Override
    public void onInitialize() {
        AlmostLibCommon.init();
        List<EntrypointContainer<AlmostLibInitializer>> containers = FabricLoader
                .getInstance()
                .getEntrypointContainers(INIT_KEY, AlmostLibInitializer.class);

        for (EntrypointContainer<AlmostLibInitializer> container : containers) {
            container.getEntrypoint().onInitialize();
        }

        for (RegistryManager manager : AlmostLibCommon.MANAGERS) {
            manager.init();
        }
    }

    @Override
    public void onInitializeClient() {
        List<EntrypointContainer<AlmostLibInitializer>> containers = FabricLoader
                .getInstance()
                .getEntrypointContainers(INIT_KEY, AlmostLibInitializer.class);

        for (EntrypointContainer<AlmostLibInitializer> container : containers) {
            container.getEntrypoint().onClientInitialize();
        }

        for (RegistryManager manager : AlmostLibCommon.MANAGERS) {
            manager.initClient();
        }
    }

    @Override
    public void onInitializeDataGenerator(FabricDataGenerator dataGenerator) {
        for (RegistryManager manager : AlmostLibCommon.MANAGERS) {
            manager.onDataGen(dataGenerator);
        }
    }
}
