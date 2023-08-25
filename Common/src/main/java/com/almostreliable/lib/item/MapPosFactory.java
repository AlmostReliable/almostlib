package com.almostreliable.lib.item;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;

import javax.annotation.Nullable;

@FunctionalInterface
public interface MapPosFactory {

    @Nullable
    Info apply(ServerLevel level, BlockPos entity);

    record Info(BlockPos pos, Component name) {

    }
}
