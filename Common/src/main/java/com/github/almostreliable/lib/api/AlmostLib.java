package com.github.almostreliable.lib.api;

import com.github.almostreliable.lib.api.registry.RegistryManager;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;

import java.util.function.Consumer;
import java.util.function.Supplier;

public interface AlmostLib {
    AlmostLib INSTANCE = ServiceLoader.load(AlmostLib.class);

    String getPlatformName();

    boolean isModLoaded(String modId);

    boolean isDevelopmentEnvironment();

    CreativeModeTab createCreativeTab(ResourceLocation location, Supplier<ItemStack> supplier);

    RegistryManager createRegistry(String namespace);

    void openMenu(ServerPlayer player, MenuProvider menu, Consumer<FriendlyByteBuf> buf);

    default void openMenu(ServerPlayer player, MenuProvider menu, BlockPos blockPos) {
        this.openMenu(player, menu, buf -> buf.writeBlockPos(blockPos));
    }
}
