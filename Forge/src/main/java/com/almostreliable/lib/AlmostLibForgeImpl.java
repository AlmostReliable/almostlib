package com.almostreliable.lib;

import com.almostreliable.lib.client.MenuFactory;
import com.almostreliable.lib.registry.AlmostManager;
import com.almostreliable.lib.registry.AlmostManagerForge;
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
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.extensions.IForgeMenuType;
import net.minecraftforge.data.loading.DatagenModLoader;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.loading.FMLLoader;
import net.minecraftforge.gametest.ForgeGameTestHooks;
import net.minecraftforge.network.NetworkHooks;
import net.minecraftforge.registries.ForgeRegistries;

import javax.annotation.Nonnull;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.stream.Stream;

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
    public boolean isClient() {
        return FMLLoader.getDist() == Dist.CLIENT;
    }

    @Override
    public CreativeModeTab createCreativeTab(ResourceLocation location, Supplier<ItemStack> supplier) {
        String string = String.format("%s.%s", location.getNamespace(), location.getPath());
        return new CreativeModeTab(string) {
            @Override
            public ItemStack makeIcon() {
                return supplier.get();
            }
        };
    }

    @Override
    public AlmostManager createManager(String namespace) {
        return new AlmostManagerForge(namespace);
    }

    @Override
    public <T extends BlockEntity> BlockEntityType<T> createBlockEntityType(@Nonnull BiFunction<BlockPos, BlockState, T> factory, Block... blocks) {
        //noinspection NullableProblems,ConstantConditions
        return BlockEntityType.Builder.of(factory::apply, blocks).build(null);
    }

    @Override
    public <T extends AbstractContainerMenu> MenuType<T> createMenuType(MenuFactory<T> factory) {
        return IForgeMenuType.create(factory::apply);
    }

    @Override
    public Stream<? extends Block> getBlocks() {
        return ForgeRegistries.BLOCKS.getValues().stream();
    }

    @Override
    public void openMenu(ServerPlayer player, MenuProvider menu, Consumer<FriendlyByteBuf> bufferCallback) {
        NetworkHooks.openGui(player, menu, bufferCallback);
    }

    @Override
    public boolean isGameTestMode() {
        return ForgeGameTestHooks.isGametestEnabled();
    }

    @Override
    public boolean isDataGenEnabled() {
        return DatagenModLoader.isRunningDataGen();
    }

}
