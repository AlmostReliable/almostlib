package com.almostreliable.almostlib;

import com.almostreliable.almostlib.datagen.DataGenManager;
import com.almostreliable.almostlib.item.AlmostCreativeTab;
import com.almostreliable.almostlib.registry.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;

/**
 * Simple manager to hold registrations for a specified namespace.
 */
public class AlmostManager {

    private final String namespace;
    private final DataGenManager dataGen;
    private final ItemRegistration items;
    private final BlockRegistration blocks;
    private final BlockEntityRegistration blockEntities;
    private final MenuRegistration menus;

    private AlmostManager(String namespace) {
        this.namespace = namespace;
        this.dataGen = DataGenManager.create(namespace);
        this.items = Registration.items(namespace).dataGen(dataGen);
        this.blocks = Registration.blocks(namespace).itemRegistration(items).dataGen(dataGen);
        this.blockEntities = Registration.blockEntities(namespace);
        this.menus = Registration.menus(namespace);
    }

    /**
     * Creates a new AlmostManager for the given namespace.
     *
     * @param namespace The namespace to use for this manager.
     * @return A new AlmostManager.
     */
    public static AlmostManager create(String namespace) {
        return new AlmostManager(namespace);
    }

    /**
     * Sets the default creative tab for all items registered by this manager.
     * <p>
     * Can be overwritten per item by calling {@link ItemRegistration.Builder#creativeTab(CreativeModeTab)}.
     *
     * @param tab The creative tab to use.
     * @return The instance of this manager.
     */
    public AlmostManager defaultCreativeTab(AlmostCreativeTab tab) {
        items.defaultCreativeTab(tab);
        tab.bindLang(dataGen);
        return this;
    }

    /**
     * Gets the namespace that is assigned to this manager.
     *
     * @return The namespace.
     */
    public String getNamespace() {
        return namespace;
    }

    /**
     * Creates a new {@link ResourceLocation} for the given path within this manager's namespace.
     *
     * @param path The path to use.
     * @return A new {@link ResourceLocation}.
     */
    public ResourceLocation getRL(String path) {
        return new ResourceLocation(namespace, path);
    }

    /**
     * Gets the {@link ItemRegistration} for this manager.
     *
     * @return The {@link ItemRegistration}.
     */
    public ItemRegistration items() {
        return items;
    }

    /**
     * Gets the {@link BlockRegistration} for this manager.
     *
     * @return The {@link BlockRegistration}.
     */
    public BlockRegistration blocks() {
        return blocks;
    }

    /**
     * Gets the {@link BlockEntityRegistration} for this manager.
     *
     * @return The {@link BlockEntityRegistration}.
     */
    public BlockEntityRegistration blockEntities() {
        return blockEntities;
    }

    public MenuRegistration menus() {
        return menus;
    }

    /**
     * Gets the {@link DataGenManager} for this manager.
     *
     * @return The {@link DataGenManager}.
     */
    public DataGenManager dataGen() {
        return dataGen;
    }

    /**
     * Initializes the registries held by this manager to the platform this method
     * is called on.
     * <br>
     * This has to be called after all objects have been registered to the manager.
     */
    public void initRegistriesToLoader() {
        AlmostLib.PLATFORM.initRegistrations(items, blocks, blockEntities, menus);
    }
}
