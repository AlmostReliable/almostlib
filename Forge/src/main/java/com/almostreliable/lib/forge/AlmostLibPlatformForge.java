package com.almostreliable.lib.forge;

import com.almostreliable.lib.AlmostLibPlatform;
import com.almostreliable.lib.Platform;
import com.almostreliable.lib.client.MenuFactory;
import com.almostreliable.lib.forge.network.NetworkHandlerForge;
import com.almostreliable.lib.network.NetworkHandler;
import com.almostreliable.lib.registry.Registration;
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
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.extensions.IForgeMenuType;
import net.minecraftforge.data.loading.DatagenModLoader;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLLoader;
import net.minecraftforge.fml.loading.FMLPaths;
import net.minecraftforge.gametest.ForgeGameTestHooks;
import net.minecraftforge.network.NetworkHooks;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.RegisterEvent;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.nio.file.Path;
import java.util.function.BiConsumer;
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
    public boolean isGameTestEnabled() {
        return ForgeGameTestHooks.isGametestEnabled();
    }

    @Override
    public CreativeModeTab createCreativeTab(ResourceLocation location, Supplier<ItemStack> icon) {
        String id = String.format("%s.%s", location.getNamespace(), location.getPath());
        return new CreativeModeTab(id) {
            @Override
            @Nonnull
            public ItemStack makeIcon() {
                return icon.get();
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
        NetworkHooks.openScreen(player, menu, buf);
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
    public <T> TagKey<T> createTag(ResourceKey<Registry<T>> resourceKey, String $, String forgeTag) {
        return createTag(resourceKey, forgeTag);
    }

    @Override
    public <T> void initRegistration(Registration<T, ?> registration) {
        FMLJavaModLoadingContext
            .get()
            .getModEventBus()
            .addListener((RegisterEvent event) -> {
                if (!registration.getRegistry().key().equals(event.getRegistryKey())) {
                    return;
                }

                BiConsumer<ResourceLocation, Object> registerConsumer = createRegisterConsumer(event.getForgeRegistry(),
                    event.getVanillaRegistry());
                if (registerConsumer == null) {
                    throw new IllegalArgumentException("Neither forge registry nor vanilla registry is present");
                }
                registration.applyRegister(registerConsumer::accept);
            });
    }

    @Nullable
    private BiConsumer<ResourceLocation, Object> createRegisterConsumer(@Nullable IForgeRegistry<Object> forgeRegistry, @Nullable Registry<Object> vanillaRegistry) {
        if (forgeRegistry != null) {
            return forgeRegistry::register;
        }
        if (vanillaRegistry != null) {
            return (location, object) -> Registry.register(vanillaRegistry, location, object);
        }
        return null;
    }

    @Override
    public void initRegistrations(Registration<?, ?>... registrations) {
        for (Registration<?, ?> registration : registrations) {
            initRegistration(registration);
        }
    }

    @Override
    public void registerRecipeSerializers(ResourceLocation id, RecipeSerializer<? extends Recipe<?>> serializer) {
        ForgeRegistries.RECIPE_SERIALIZERS.register(id, serializer);
    }

    @Override
    public NetworkHandler createNetworkHandler(ResourceLocation id) {
        return new NetworkHandlerForge(id);
    }
}
