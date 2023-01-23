package com.almostreliable.almostlib.menu.synchronizer.handler;

import com.almostreliable.almostlib.menu.synchronizer.AbstractDataHandler;
import net.minecraft.network.FriendlyByteBuf;

import java.util.function.Consumer;
import java.util.function.Supplier;

public class LongDataHandler extends AbstractDataHandler<Long> {

    protected LongDataHandler(Supplier<Long> getter, Consumer<Long> setter) {
        super(getter, setter);
    }

    @Override
    protected void handleEncoding(FriendlyByteBuf buffer, Long value) {
        buffer.writeLong(value);
    }

    @Override
    protected Long handleDecoding(FriendlyByteBuf buffer) {
        return buffer.readLong();
    }
}
