package com.almostreliable.lib;

import com.almostreliable.lib.client.MenuFactory;
import com.almostreliable.lib.registry.AlmostManager;
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
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.stream.Stream;

public interface AlmostLib {
    AlmostLib INSTANCE = ServiceLoader.load(AlmostLib.class);

    Platform getPlatform();

    String getPlatformName();

    boolean isModLoaded(String modId);

    boolean isDevelopmentEnvironment();

    boolean isClient();

    CreativeModeTab createCreativeTab(ResourceLocation location, Supplier<ItemStack> supplier);

    AlmostManager createManager(String namespace);

    <T extends BlockEntity> BlockEntityType<T> createBlockEntityType(BiFunction<BlockPos, BlockState, T> factory, Block... blocks);

    <M extends AbstractContainerMenu> MenuType<M> createMenuType(MenuFactory<M> factory);

    Stream<? extends Block> getBlocks();

    void openMenu(ServerPlayer player, MenuProvider menu, Consumer<FriendlyByteBuf> buf);

    default void openMenu(ServerPlayer player, MenuProvider menu, BlockPos blockPos) {
        this.openMenu(player, menu, buf -> buf.writeBlockPos(blockPos));
    }

    boolean isGameTestMode();

    boolean isDataGenEnabled();

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
}
