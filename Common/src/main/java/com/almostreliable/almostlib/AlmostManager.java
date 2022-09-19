package com.almostreliable.almostlib;

import com.almostreliable.almostlib.datagen.DataGenManager;
import com.almostreliable.almostlib.registry.BlockEntityRegistration;
import com.almostreliable.almostlib.registry.BlockRegistration;
import com.almostreliable.almostlib.registry.ItemRegistration;
import com.almostreliable.almostlib.registry.Registration;

/**
 * Simple manager which holds registrations for a mod.
 */
public class AlmostManager {

    private final String namespace;
    private final DataGenManager dataGen;
    private final ItemRegistration items;
    private final BlockRegistration blocks;
    private final BlockEntityRegistration blockEntities;

    private AlmostManager(String namespace, String creativeTabDescription) {
        this.namespace = namespace;
        this.dataGen = DataGenManager.create(namespace);
        this.items = Registration.items(namespace).dataGen(dataGen).initDefaultCreativeTab(creativeTabDescription);
        this.blocks = Registration.blocks(namespace).itemRegistration(items).dataGen(dataGen);
        this.blockEntities = Registration.blockEntities(namespace);
    }

    public static AlmostManager create(String namespace, String creativeTabDescription) {
        return new AlmostManager(namespace, creativeTabDescription);
    }

    public String getNamespace() {
        return namespace;
    }

    public ItemRegistration items() {
        return items;
    }

    public BlockRegistration blocks() {
        return blocks;
    }

    public BlockEntityRegistration blockEntities() {
        return blockEntities;
    }

    public DataGenManager dataGen() {
        return dataGen;
    }

    public void initRegistriesToLoader() {
        AlmostLib.PLATFORM.initRegistrations(items, blocks, blockEntities);
    }
}