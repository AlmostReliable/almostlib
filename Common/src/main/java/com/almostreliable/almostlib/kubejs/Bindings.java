package com.almostreliable.almostlib.kubejs;

import com.almostreliable.almostlib.util.LevelUtils;
import com.almostreliable.almostlib.util.ResourceOrTag;
import com.almostreliable.almostlib.util.WeightedList;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.levelgen.feature.ConfiguredStructureFeature;

import javax.annotation.Nullable;

public class Bindings {

    @Nullable
    public static BlockPos findStructure(BlockPos position, ServerLevel level, String structure, int chunkRadius) {
        return LevelUtils.findStructure(position,
                level,
                ResourceOrTag.get(structure, Registry.CONFIGURED_STRUCTURE_FEATURE_REGISTRY),
                chunkRadius,
                true);
    }

    @Nullable
    public static BlockPos findStructure(BlockPos position, ServerLevel level, String structure, int chunkRadius, boolean skipKnownStructures) {
        ResourceOrTag<ConfiguredStructureFeature<?, ?>> rot = ResourceOrTag.get(structure,
                Registry.CONFIGURED_STRUCTURE_FEATURE_REGISTRY);
        return LevelUtils.findStructure(position, level, rot, chunkRadius, skipKnownStructures);
    }

    @Nullable
    public static BlockPos findBiome(BlockPos position, ServerLevel level, String biome, int chunkRadius) {
        ResourceOrTag<Biome> rot = ResourceOrTag.get(biome, Registry.BIOME_REGISTRY);
        return LevelUtils.findBiome(position, level, rot, chunkRadius);
    }

    public static WeightedList.Builder<Object> weightedList() {
        return new WeightedList.Builder<>();
    }
}
