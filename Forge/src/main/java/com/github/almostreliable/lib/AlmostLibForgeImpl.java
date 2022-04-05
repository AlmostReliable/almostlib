package com.github.almostreliable.lib;

import com.github.almostreliable.lib.api.AlmostLib;
import com.github.almostreliable.lib.api.registry.IAlmostRegistry;
import com.github.almostreliable.lib.api.registry.RegistryManager;
import com.github.almostreliable.lib.registry.ForgeAlmostRegistry;
import com.github.almostreliable.lib.registry.RegistryManagerForge;
import net.minecraft.core.Registry;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.loading.FMLLoader;
import net.minecraftforge.network.NetworkHooks;
import net.minecraftforge.registries.ForgeRegistry;

import java.util.function.Consumer;
import java.util.function.Supplier;

public class AlmostLibForgeImpl implements AlmostLib {

    @Override
    public String getPlatformName() {
        return "Forge";
    }

    @Override
    public boolean isModLoaded(String modId) {
        return ModList.get().isLoaded(modId);
    }

    @Override
    public boolean isDevelopmentEnvironment() {
        return !FMLLoader.isProduction();
    }

    @Override
    public CreativeModeTab createCreativeTab(ResourceLocation location, Supplier<ItemStack> supplier) {
        return new CreativeModeTab(location.toString()) {
            @Override
            public ItemStack makeIcon() {
                return supplier.get();
            }
        };
    }

    @Override
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public <T> IAlmostRegistry<T> createRegistry(String namespace, ResourceKey<Registry<T>> resourceKey) {
        ForgeRegistry registry = net.minecraftforge.registries.RegistryManager.ACTIVE.getRegistry(resourceKey.location());
        return (IAlmostRegistry<T>) new ForgeAlmostRegistry(namespace, registry);
    }

    @Override
    public RegistryManager createRegistry(String namespace) {
        return new RegistryManagerForge(namespace);
    }

    @Override
    public void openMenu(ServerPlayer player, MenuProvider menu, Consumer<FriendlyByteBuf> bufferCallback) {
        NetworkHooks.openGui(player, menu, bufferCallback);
    }

}
