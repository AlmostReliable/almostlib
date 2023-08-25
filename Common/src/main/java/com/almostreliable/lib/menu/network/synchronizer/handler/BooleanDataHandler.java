package com.almostreliable.lib.menu.network.synchronizer.handler;

import com.almostreliable.lib.menu.network.synchronizer.AbstractDataHandler;
import net.minecraft.network.FriendlyByteBuf;

import java.util.function.Consumer;
import java.util.function.Supplier;

public class BooleanDataHandler extends AbstractDataHandler<Boolean> {

    protected BooleanDataHandler(Supplier<Boolean> getter, Consumer<Boolean> setter) {
        super(getter, setter);
    }

    @Override
    protected void handleEncoding(FriendlyByteBuf buffer, Boolean value) {
        buffer.writeBoolean(value);
    }

    @Override
    protected Boolean handleDecoding(FriendlyByteBuf buffer) {
        return buffer.readBoolean();
    }
}
