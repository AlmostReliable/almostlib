package com.almostreliable.almostlib.util.filter;

import com.almostreliable.almostlib.mixin.BlockPredicateAccessor;
import com.almostreliable.almostlib.util.AlmostUtils;
import net.minecraft.advancements.critereon.NbtPredicate;
import net.minecraft.advancements.critereon.StatePropertiesPredicate;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

import javax.annotation.Nullable;
import java.util.Set;
import java.util.function.Predicate;

public class BlockPredicate implements Predicate<BlockState> {
// TODO
    /**
     * {@link net.minecraft.advancements.critereon.BlockPredicate}
     * {@link net.minecraft.advancements.critereon.StatePropertiesPredicate}
     */
    public static final BlockPredicate ANY = new BlockPredicate(null, null, StatePropertiesPredicate.ANY, NbtPredicate.ANY);

    private final BlockPredicateAccessor delegate;

    public BlockPredicate(@Nullable TagKey<Block> tag, @Nullable Set<Block> blocks, StatePropertiesPredicate properties, NbtPredicate nbt) {
        this.delegate = AlmostUtils.cast(new net.minecraft.advancements.critereon.BlockPredicate(tag, blocks, properties, nbt));
    }

    @SuppressWarnings("RedundantIfStatement")
    @Override
    public boolean test(BlockState state) {
        if (equals(ANY)) return true;
        if (delegate.getTag() != null && !state.is(delegate.getTag())) return false;
        if (delegate.getBlocks() != null && !delegate.getBlocks().contains(state.getBlock())) return false;
        if (!getProperties().matches(state)) return false;
        return true;
    }

    public boolean test(ServerLevel level, BlockPos pos) {
        return delegate.matches(level, pos);
    }

    public StatePropertiesPredicate getProperties() {
        return delegate.getProperties();
    }

    public NbtPredicate getNbt() {
        return delegate.getNbt();
    }
}
