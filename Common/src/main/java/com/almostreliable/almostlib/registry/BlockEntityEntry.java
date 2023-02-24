package com.almostreliable.almostlib.registry;

import com.almostreliable.almostlib.util.AlmostUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

import javax.annotation.Nullable;
import java.util.Optional;
import java.util.function.Supplier;

public class BlockEntityEntry<BE extends BlockEntity> extends RegistryEntryImpl<BlockEntityType<BE>> {

    public BlockEntityEntry(ResourceLocation id, Supplier<BlockEntityType<BE>> supplier) {
        super(AlmostUtils.cast(Registry.BLOCK_ENTITY_TYPE), id, supplier);
    }

    @Nullable
    public BE get(BlockGetter lookup, BlockPos pos) {
        return get().getBlockEntity(lookup, pos);
    }

    public Optional<BE> getOptional(BlockGetter lookup, BlockPos pos) {
        return Optional.ofNullable(get().getBlockEntity(lookup, pos));
    }

    public BE create(BlockPos pos, BlockState state) {
        return get().create(pos, state);
    }
}
