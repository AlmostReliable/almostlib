package com.almostreliable.almostlib;

import com.almostreliable.almostlib.client.MenuFactory;
import com.almostreliable.almostlib.network.NetworkHandler;
import com.almostreliable.almostlib.registry.Registration;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.TagKey;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

import java.nio.file.Path;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Supplier;

public interface AlmostLibPlatform {
    /**
     * Gets the current platform
     *
     * @return The current platform.
     */
    Platform getPlatform();

    /**
     * Checks if a mod with the given id is loaded.
     *
     * @param modId The mod to check if it is loaded.
     * @return True if the mod is loaded, false otherwise.
     */
    boolean isModLoaded(String modId);

    /**
     * Check if the game is currently in a development environment.
     *
     * @return True if in a development environment, false otherwise.
     */
    boolean isDevelopmentEnvironment();

    boolean isClient();

    CreativeModeTab createCreativeTab(ResourceLocation location, Supplier<ItemStack> supplier);

    <T extends BlockEntity> BlockEntityType<T> createBlockEntityType(BiFunction<BlockPos, BlockState, T> factory, Block... blocks);

    <M extends AbstractContainerMenu> MenuType<M> createMenuType(MenuFactory<M> factory);

    void openMenu(ServerPlayer player, MenuProvider menu, Consumer<FriendlyByteBuf> buf);

    default void openMenu(ServerPlayer player, MenuProvider menu, BlockPos blockPos) {
        this.openMenu(player, menu, buf -> buf.writeBlockPos(blockPos));
    }

    default void openMenu(ServerPlayer player, MenuProvider menu, BlockPos blockPos, Consumer<FriendlyByteBuf> buf) {
        this.openMenu(player, menu, firstBuf -> {
            firstBuf.writeBlockPos(blockPos);
            buf.accept(firstBuf);
        });
    }

    boolean isDataGenEnabled();

    Path getConfigPath();

    Path getRootPath();

    /**
     * Creates a platform-specific tag.
     * Forge: tags will be created in the "forge" namespace.
     * Fabric: tags will be created in the "c" namespace.
     *
     * @param resourceKey The resource key of the tag.
     * @param tag         The tag.
     * @param <T>         The type of the tag.
     * @return The created tag.
     */
    <T> TagKey<T> createTag(ResourceKey<Registry<T>> resourceKey, String tag);

    default TagKey<Item> createItemTag(String tag) {
        return this.createTag(Registry.ITEM_REGISTRY, tag);
    }

    default TagKey<Block> createBlockTag(String tag) {
        return this.createTag(Registry.BLOCK_REGISTRY, tag);
    }

    default TagKey<EntityType<?>> createEntityTag(String tag) {
        return this.createTag(Registry.ENTITY_TYPE_REGISTRY, tag);
    }

    <T> void initRegistration(Registration<T, ?> registration);

    /**
     * Init multiple registration at once. For Fabric, the lib will sort the registrations by priority. Forge does this automatically.
     *
     * @param registrations The registrations to init.
     */
    void initRegistrations(Registration<?, ?>... registrations);

    void registerRecipeSerializers(ResourceLocation id, RecipeSerializer<? extends Recipe<?>> serializer);

    NetworkHandler createNetworkHandler(ResourceLocation id);
}
