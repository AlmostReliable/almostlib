package com.almostreliable.lib.fabric;

import com.almostreliable.lib.AlmostLibPlatform;
import com.almostreliable.lib.Platform;
import com.almostreliable.lib.client.MenuFactory;
import com.almostreliable.lib.fabric.network.NetworkHandlerFabric;
import com.almostreliable.lib.network.NetworkHandler;
import com.almostreliable.lib.registry.Registration;
import com.google.auto.service.AutoService;
import com.google.gson.JsonObject;
import net.fabricmc.api.EnvType;
import net.fabricmc.fabric.api.client.itemgroup.FabricItemGroupBuilder;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.fabricmc.fabric.api.resource.conditions.v1.ConditionJsonProvider;
import net.fabricmc.fabric.api.resource.conditions.v1.DefaultResourceConditions;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerFactory;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerType;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.TagKey;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
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

import javax.annotation.Nullable;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Supplier;


@AutoService(AlmostLibPlatform.class)
public class AlmostLibPlatformFabric implements AlmostLibPlatform {

    @Override
    public Platform getPlatform() {
        return Platform.FABRIC;
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
    public boolean isClient() {
        return FabricLoader.getInstance().getEnvironmentType() == EnvType.CLIENT;
    }

    @Override
    public boolean isGameTestEnabled() {
        var gametests = System.getProperty("fabric-api.gametest");
        return "1".equals(gametests) || "true".equals(gametests);
    }


    @Override
    public CreativeModeTab createCreativeTab(ResourceLocation id, Supplier<ItemStack> icon) {
        return FabricItemGroupBuilder.build(id, icon);
    }

    @Override
    public <T extends BlockEntity> BlockEntityType<T> createBlockEntityType(BiFunction<BlockPos, BlockState, T> factory, Block... blocks) {
        return FabricBlockEntityTypeBuilder.create(factory::apply, blocks).build();
    }

    @Override
    public <M extends AbstractContainerMenu> MenuType<M> createMenuType(MenuFactory<M> factory) {
        return new ExtendedScreenHandlerType<>(factory::apply);
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

    @Override
    public boolean isDataGenEnabled() {
        return System.getProperty("fabric-api.datagen") != null;
    }

    @Override
    public Path getConfigPath() {
        return FabricLoader.getInstance().getConfigDir();
    }

    @Override
    public Path getRootPath() {
        return FabricLoader.getInstance().getGameDir();
    }

    @Override
    public <T> TagKey<T> createTag(ResourceKey<Registry<T>> resourceKey, String tag) {
        return TagKey.create(resourceKey, new ResourceLocation("c", tag));
    }

    @Override
    public <T> TagKey<T> createTag(ResourceKey<Registry<T>> resourceKey, String fabricTag, String $) {
        return createTag(resourceKey, fabricTag);
    }

    @Override
    public <T> void initRegistration(Registration<T, ?> registration) {
        Registry<T> registry = registration.getRegistry();
        registration.applyRegister((id, entry) -> Registry.register(registry, id, entry));
    }

    @Override
    public void initRegistrations(Registration<?, ?>... registrations) {
        List<Registry<?>> priority = List.of(
            Registry.BLOCK,
            Registry.FLUID,
            Registry.ITEM,
            Registry.BLOCK_ENTITY_TYPE
        );

        List<Registration<?, ?>> sorted = Arrays
            .stream(registrations)
            .sorted((o1, o2) -> o1
                .getRegistry()
                .key()
                .location()
                .toString()
                .compareToIgnoreCase(o2.getRegistry().key().location().toString()))
            .sorted(Comparator.comparingInt(r -> {
                int i = priority.indexOf(r.getRegistry());
                return i >= 0 ? i : priority.size();
            })).toList();

        sorted.forEach(this::initRegistration);
    }

    @Override
    public void registerRecipeSerializers(ResourceLocation id, RecipeSerializer<? extends Recipe<?>> serializer) {
        Registry.register(Registry.RECIPE_SERIALIZER, id, serializer);
    }

    @Override
    public NetworkHandler createNetworkHandler(ResourceLocation id) {
        return new NetworkHandlerFabric(id);
    }

    @Override
    public void writeRecipeModConditions(JsonObject json, Set<String> modIds) {
        var conditions = DefaultResourceConditions.allModsLoaded(modIds.toArray(new String[0]));
        ConditionJsonProvider.write(json, conditions);
    }
}
