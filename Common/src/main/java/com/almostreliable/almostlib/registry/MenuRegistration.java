package com.almostreliable.almostlib.registry;

import com.almostreliable.almostlib.AlmostLib;
import com.almostreliable.almostlib.client.MenuFactory;
import com.almostreliable.almostlib.util.AlmostUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.level.block.entity.BlockEntity;

public class MenuRegistration extends GenericRegistration<MenuType<? extends AbstractContainerMenu>> {

    MenuRegistration(String namespace, Registry<MenuType<?>> registry) {
        super(namespace, registry);
    }

    /**
     * Registers a menu type for given {@link MenuFactory}.
     *
     * <pre> {@code
     * register("id", (containerId, inventory, buffer) -> {
     *   var data = readDataFromBuffer(buffer);
     *   return new ExampleMenu(containerId, inventory, data);
     * });
     * }</pre>
     *
     * @param id      The id of the menu type
     * @param factory The factory for creating the menu
     * @param <M>     The type of the menu, extends {@link AbstractContainerMenu}
     * @param <T>     The type of the menu type {@link MenuType<M>}
     * @return The registered menu type
     */
    public <M extends AbstractContainerMenu, T extends MenuType<? extends M>> RegistryEntry<T> register(String id, MenuFactory<M> factory) {
        return register(id, () -> {
            MenuType<M> mt = AlmostLib.PLATFORM.createMenuType(factory);
            return AlmostUtils.cast(mt);
        });
    }

    /**
     * Registers a menu type for given {@link MenuFactory.ForBlockEntity}.
     *
     * <pre> {@code
     * class ExampleMenu extends AbstractContainerMenu {
     *     public ExampleMenu(int containerId, Inventory inv, ExampleBlockEntity be) {}
     * }
     *
     * register("id", ExampleBlockEntity.class, ExampleMenu::new);
     * }</pre>
     *
     * @param id      The id of the menu type
     * @param factory The factory for creating the menu
     * @param <M>     The type of the menu, extends {@link AbstractContainerMenu}
     * @param <T>     The type of the menu type {@link MenuType<M>}
     * @return The registered menu type
     */
    public <M extends AbstractContainerMenu, T extends MenuType<? extends M>, BE extends BlockEntity> RegistryEntry<T> forBlockEntity(String id, Class<BE> entityType, MenuFactory.ForBlockEntity<M, BE> factory) {
        return register(id, (wid, inventory, buffer) -> {
            BE be = getBlockEntity(entityType, inventory, buffer);
            return factory.apply(wid, inventory, be);
        });
    }

    /**
     * Registers a menu type for given {@link MenuFactory.ForCustom}.
     *
     * <pre> {@code
     * class ExampleMenu extends AbstractContainerMenu {
     *     public ExampleMenu(int containerId, Inventory inv, ExampleBlockEntity be) {}
     *
     *     public void setData(Data data) {}
     * }
     *
     * register("id", ExampleBlockEntity.class, (containerId, inventory, be, buffer) -> {
     *     var menu = new ExampleMenu(containerId, inventory, be);
     *     menu.setData(readDataFromBuffer(buffer));
     *     return menu;
     * }));
     * }</pre>
     *
     * @param id      The id of the menu type
     * @param factory The factory for creating the menu
     * @param <M>     The type of the menu, extends {@link AbstractContainerMenu}
     * @param <T>     The type of the menu type {@link MenuType<M>}
     * @return The registered menu type
     */
    public <M extends AbstractContainerMenu, T extends MenuType<? extends M>, BE extends BlockEntity> RegistryEntry<T> forCustom(String id, Class<BE> entityType, MenuFactory.ForCustom<M, BE> factory) {
        return register(id, (wid, inventory, buffer) -> {
            BE be = getBlockEntity(entityType, inventory, buffer);
            return factory.apply(wid, inventory, be, buffer);
        });
    }

    private <BE extends BlockEntity> BE getBlockEntity(Class<BE> entityType, Inventory inventory, FriendlyByteBuf buffer) {
        BlockPos blockPos = buffer.readBlockPos();
        BlockEntity be = inventory.player.level.getBlockEntity(blockPos);
        if (be == null) {
            throw new IllegalStateException("BlockEntity at " + blockPos + " was null!");
        }
        return entityType.cast(be);
    }
}
