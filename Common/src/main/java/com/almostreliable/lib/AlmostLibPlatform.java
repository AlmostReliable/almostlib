package com.almostreliable.lib;

import com.almostreliable.lib.client.MenuFactory;
import com.almostreliable.lib.network.NetworkHandler;
import com.almostreliable.lib.registry.Registration;
import com.google.gson.JsonObject;
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
import java.util.Set;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Supplier;

public interface AlmostLibPlatform {

    /**
     * Returns the current platform
     *
     * @return The current platform.
     */
    Platform getPlatform();

    /**
     * Returns if a mod with the given id is loaded.
     *
     * @param modId The mod id to check.
     * @return True if the mod is loaded, false otherwise.
     */
    boolean isModLoaded(String modId);

    /**
     * Returns if the game is currently in a development environment.
     *
     * @return True if in a development environment, false otherwise.
     */
    boolean isDevelopmentEnvironment();

    /**
     * Returns if the game is currently in a client environment.
     *
     * @return True if in a client environment, false otherwise.
     */
    boolean isClient();

    /**
     * Returns whether the game test framework is enabled.
     *
     * @return True if the game test framework is enabled, false otherwise.
     */
    boolean isGameTestEnabled();

    /**
     * Creates a new creative tab with the given id and icon supplier.
     *
     * @param id   The id of the creative tab.
     * @param icon The icon supplier of the creative tab.
     * @return The created creative tab.
     */
    CreativeModeTab createCreativeTab(ResourceLocation id, Supplier<ItemStack> icon);

    <T extends BlockEntity> BlockEntityType<T> createBlockEntityType(BiFunction<BlockPos, BlockState, T> factory, Block... blocks);

    <M extends AbstractContainerMenu> MenuType<M> createMenuType(MenuFactory<M> factory);

    void openMenu(ServerPlayer player, MenuProvider menu, Consumer<FriendlyByteBuf> buf);

    default void openMenu(ServerPlayer player, MenuProvider menu, BlockPos blockPos) {
        openMenu(player, menu, buf -> buf.writeBlockPos(blockPos));
    }

    default void openMenu(ServerPlayer player, MenuProvider menu, BlockPos blockPos, Consumer<FriendlyByteBuf> buf) {
        openMenu(player, menu, firstBuf -> {
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

    /**
     * Creates a platform-specific tag.
     * Forge: tags will be created in the "forge" namespace.
     * Fabric: tags will be created in the "c" namespace.
     *
     * @param resourceKey The resource key of the tag.
     * @param fabricTag   The fabric tag.
     * @param forgeTag    The forge tag.
     * @param <T>         The type of the tag.
     * @return The created tag.
     */
    <T> TagKey<T> createTag(ResourceKey<Registry<T>> resourceKey, String fabricTag, String forgeTag);

    default TagKey<Item> createItemTag(String tag) {
        return this.createTag(Registry.ITEM_REGISTRY, tag);
    }

    default TagKey<Item> createItemTag(String fabricTag, String forgeTag) {
        return this.createTag(Registry.ITEM_REGISTRY, fabricTag, forgeTag);
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

    /**
     * Writes the mod-loaded conditions to the json.
     *
     * @param json The json to write to.
     * @param modIds The mod ids to write.
     */
    void writeRecipeModConditions(JsonObject json, Set<String> modIds);
}
