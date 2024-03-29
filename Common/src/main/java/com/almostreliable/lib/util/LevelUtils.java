package com.almostreliable.lib.util;

import com.mojang.datafixers.util.Pair;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.levelgen.structure.Structure;

import javax.annotation.Nullable;

public class LevelUtils {

    @Nullable
    public static BlockPos findStructure(BlockPos position, ServerLevel level, ResourceOrTag<Structure> rot, int chunkRadius, boolean skipKnownStructures) {
        return level
            .registryAccess()
            .registry(Registry.STRUCTURE_REGISTRY)
            .flatMap(rot::asHolderSet)
            .map(holderSet -> level
                .getChunkSource()
                .getGenerator()
                .findNearestMapStructure(level, holderSet, position, chunkRadius, skipKnownStructures))
            .map(Pair::getFirst)
            .orElse(null);
    }

    @Nullable
    public static BlockPos findBiome(BlockPos position, ServerLevel level, ResourceOrTag<Biome> rot, int chunkRadius) {
        Pair<BlockPos, Holder<Biome>> nearestBiome = level.findClosestBiome3d(rot.asHolderPredicate(),
            position,
            chunkRadius * 16,
            32,
            64);
        if (nearestBiome != null) {
            return nearestBiome.getFirst();
        }
        return null;
    }
}
