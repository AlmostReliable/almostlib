package com.almostreliable.almostlib.network.synchronizer.handler;

import com.almostreliable.almostlib.network.synchronizer.AbstractDataHandler;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;

import java.util.function.Consumer;
import java.util.function.Supplier;

public class BlockPosDataHandler extends AbstractDataHandler<BlockPos> {

    protected BlockPosDataHandler(Supplier<BlockPos> getter, Consumer<BlockPos> setter) {
        super(getter, setter);
    }

    @Override
    protected void handleEncoding(FriendlyByteBuf buffer, BlockPos value) {
        buffer.writeBlockPos(value);
    }

    @Override
    protected BlockPos handleDecoding(FriendlyByteBuf buffer) {
        return buffer.readBlockPos();
    }
}
