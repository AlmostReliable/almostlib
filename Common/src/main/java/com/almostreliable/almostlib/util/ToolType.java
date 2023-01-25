package com.almostreliable.almostlib.util;

import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;

public enum ToolType {
    PICKAXE(BlockTags.MINEABLE_WITH_PICKAXE),
    AXE(BlockTags.MINEABLE_WITH_AXE),
    SHOVEL(BlockTags.MINEABLE_WITH_SHOVEL),
    HOE(BlockTags.MINEABLE_WITH_HOE);

    public final TagKey<Block> mineableBlockTag;

    ToolType(TagKey<Block> mineableBlockTag) {
        this.mineableBlockTag = mineableBlockTag;
    }
}
