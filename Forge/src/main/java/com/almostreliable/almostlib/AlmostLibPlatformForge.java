package com.almostreliable.almostlib;

import com.almostreliable.almostlib.client.MenuFactory;
import com.almostreliable.almostlib.registry.Registration;
import com.almostreliable.almostlib.util.AlmostUtils;
import com.google.auto.service.AutoService;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.TagKey;
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
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLLoader;
import net.minecraftforge.fml.loading.FMLPaths;
import net.minecraftforge.network.NetworkHooks;
import net.minecraftforge.registries.ForgeRegistry;
import net.minecraftforge.registries.IForgeRegistryEntry;
import net.minecraftforge.registries.RegistryManager;

import javax.annotation.Nonnull;
import java.nio.file.Path;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Supplier;

@AutoService(AlmostLibPlatform.class)
public class AlmostLibPlatformForge implements AlmostLibPlatform {

    @Override
    public Platform getPlatform() {
        return Platform.FORGE;
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
            @Nonnull
            public ItemStack makeIcon() {
                return supplier.get();
            }
        };
    }

    @Override
    public <T extends BlockEntity> BlockEntityType<T> createBlockEntityType(BiFunction<BlockPos, BlockState, T> factory, Block... blocks) {
        //noinspection NullableProblems,ConstantConditions
        return BlockEntityType.Builder.of(factory::apply, blocks).build(null);
    }

    @Override
    public <M extends AbstractContainerMenu> MenuType<M> createMenuType(MenuFactory<M> factory) {
        return IForgeMenuType.create(factory::apply);
    }

    @Override
    public void openMenu(ServerPlayer player, MenuProvider menu, Consumer<FriendlyByteBuf> buf) {
        NetworkHooks.openGui(player, menu, buf);
    }

    @Override
    public boolean isDataGenEnabled() {
        return DatagenModLoader.isRunningDataGen();
    }

    @Override
    public Path getConfigPath() {
        return FMLPaths.CONFIGDIR.get();
    }

    @Override
    public Path getRootPath() {
        return FMLPaths.GAMEDIR.get();
    }

    @Override
    public <T> TagKey<T> createTag(ResourceKey<Registry<T>> resourceKey, String tag) {
        return TagKey.create(resourceKey, new ResourceLocation("forge", tag));
    }

    @Override
    public <T> void initRegistration(Registration<T, ?> registration) {
        //noinspection rawtypes
        ForgeRegistry r = RegistryManager.ACTIVE.getRegistry(registration.getRegistry().key().location());
        if (r == null) {
            // TODO Handle this? Need to check
            throw new IllegalArgumentException(
                    "Registry " + registration.getRegistry().key().location() + " does not exist for Forge");
        }
        //noinspection unchecked
        FMLJavaModLoadingContext
                .get()
                .getModEventBus()
                .addGenericListener(r.getRegistrySuperType(), (RegistryEvent.Register<?> event) -> {
                    registration.applyRegister((id, entry) -> {
                        if (!(entry instanceof IForgeRegistryEntry<?> s)) {
                            AlmostLib.LOGGER.error("Entry {} is not an IForgeRegistryEntry", entry);
                            return;
                        }
                        s.setRegistryName(id);
                        event.getRegistry().register(AlmostUtils.cast(s));
                    });
                });
    }

    @Override
    public void initRegistrations(Registration<?, ?>... registrations) {
        for (Registration<?, ?> registration : registrations) {
            initRegistration(registration);
        }
    }
}
