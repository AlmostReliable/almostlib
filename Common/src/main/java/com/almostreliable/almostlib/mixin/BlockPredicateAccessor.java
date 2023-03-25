package com.almostreliable.almostlib.mixin;

import net.minecraft.advancements.critereon.BlockPredicate;
import net.minecraft.advancements.critereon.NbtPredicate;
import net.minecraft.advancements.critereon.StatePropertiesPredicate;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

import javax.annotation.Nullable;
import java.util.Set;

@Mixin(BlockPredicate.class)
public interface BlockPredicateAccessor {

    @Nullable
    @Accessor("tag")
    TagKey<Block> getTag();

    @Nullable
    @Accessor("blocks")
    Set<Block> getBlocks();

    @Accessor("properties")
    StatePropertiesPredicate getProperties();

    @Accessor("nbt")
    NbtPredicate getNbt();

    @Invoker("matches")
    boolean matches(ServerLevel level, BlockPos pos);
}
