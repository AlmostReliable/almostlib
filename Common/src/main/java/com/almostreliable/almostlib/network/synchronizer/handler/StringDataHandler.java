package com.almostreliable.almostlib.network.synchronizer.handler;

import com.almostreliable.almostlib.network.synchronizer.AbstractDataHandler;
import net.minecraft.network.FriendlyByteBuf;

import java.util.function.Consumer;
import java.util.function.Supplier;

public class StringDataHandler extends AbstractDataHandler<String> {

    protected StringDataHandler(Supplier<String> getter, Consumer<String> setter) {
        super(getter, setter);
    }

    @Override
    protected void handleEncoding(FriendlyByteBuf buffer, String value) {
        buffer.writeUtf(value);
    }

    @Override
    protected String handleDecoding(FriendlyByteBuf buffer) {
        return buffer.readUtf();
    }
}
