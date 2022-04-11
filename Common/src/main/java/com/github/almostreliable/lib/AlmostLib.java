package com.github.almostreliable.lib;

import com.github.almostreliable.lib.client.MenuFactory;
import com.github.almostreliable.lib.registry.RegistryManager;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.stream.Stream;

public interface AlmostLib {
    String MOD_ID = "almostlib";

    String MOD_NAME = "AlmostReliable Lib";

    Logger LOG = LoggerFactory.getLogger(MOD_NAME);

    AlmostLib INSTANCE = ServiceLoader.load(AlmostLib.class);

    String getPlatformName();

    boolean isModLoaded(String modId);

    boolean isDevelopmentEnvironment();

    CreativeModeTab createCreativeTab(ResourceLocation location, Supplier<ItemStack> supplier);

    RegistryManager createRegistry(String namespace);

    <T extends BlockEntity> BlockEntityType<T> createBlockEntityType(BiFunction<BlockPos, BlockState, T> factory, Block... blocks);

    <M extends AbstractContainerMenu> MenuType<M> createMenuType(MenuFactory<M> factory);

    Stream<? extends Block> getBlocks();

    void openMenu(ServerPlayer player, MenuProvider menu, Consumer<FriendlyByteBuf> buf);

    default void openMenu(ServerPlayer player, MenuProvider menu, BlockPos blockPos) {
        this.openMenu(player, menu, buf -> buf.writeBlockPos(blockPos));
    }

    boolean isGameTestMode();

    boolean isDataGenEnabled();
}
