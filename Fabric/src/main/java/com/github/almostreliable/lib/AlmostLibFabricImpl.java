package com.github.almostreliable.lib;

import com.github.almostreliable.lib.api.AlmostLib;
import com.github.almostreliable.lib.api.registry.IAlmostRegistry;
import com.github.almostreliable.lib.api.registry.RegistryToRuleThemAll;
import com.github.almostreliable.lib.registry.FabricAlmostRegistry;
import com.github.almostreliable.lib.registry.RegistryToRuleThemAllFabric;
import net.fabricmc.fabric.api.client.itemgroup.FabricItemGroupBuilder;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerFactory;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.core.Registry;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;
import java.util.function.Supplier;

public class AlmostLibFabricImpl implements AlmostLib {

    @Override
    public String getPlatformName() {
        return "Fabric";
    }

    @Override
    public boolean isModLoaded(String modId) {
        return FabricLoader.getInstance().isModLoaded(modId);
    }

    @Override
    public boolean isDevelopmentEnvironment() {
        return FabricLoader.getInstance().isDevelopmentEnvironment();
    }

    @Override
    public CreativeModeTab createCreativeTab(ResourceLocation location, Supplier<ItemStack> supplier) {
        return FabricItemGroupBuilder.build(location, supplier);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> IAlmostRegistry<T> createRegistry(String namespace, ResourceKey<Registry<T>> resourceKey) {
        Registry<T> registry = (Registry<T>) Registry.REGISTRY.get(resourceKey.location());
        return new FabricAlmostRegistry<>(namespace, registry);
    }

    @Override
    public RegistryToRuleThemAll createRegistry(String namespace) {
        return new RegistryToRuleThemAllFabric(namespace);
    }

    @Override
    public void openMenu(ServerPlayer player, MenuProvider menu, Consumer<FriendlyByteBuf> buf) {
        player.openMenu(new ExtendedScreenHandlerFactory() {
            @Override
            public void writeScreenOpeningData(ServerPlayer player, FriendlyByteBuf internBuf) {
                buf.accept(internBuf);
            }

            @Override
            public Component getDisplayName() {
                return menu.getDisplayName();
            }

            @Nullable
            @Override
            public AbstractContainerMenu createMenu(int i, Inventory inventory, Player player) {
                return menu.createMenu(i, inventory, player);
            }
        });
    }
}
