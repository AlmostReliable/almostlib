package com.almostreliable.lib.menu.network.synchronizer.handler;

import com.almostreliable.lib.menu.network.synchronizer.AbstractDataHandler;
import net.minecraft.network.FriendlyByteBuf;

import java.util.function.Consumer;
import java.util.function.Supplier;

public class EnumDataHandler<T extends Enum<T>> extends AbstractDataHandler<T> {

    private final T[] values;

    @SafeVarargs
    public EnumDataHandler(Supplier<T> getter, Consumer<T> setter, T... values) {
        super(getter, setter);
        this.values = values;
    }

    @Override
    protected void handleEncoding(FriendlyByteBuf buffer, T value) {
        buffer.writeShort((short) value.ordinal());
    }

    @Override
    protected T handleDecoding(FriendlyByteBuf buffer) {
        return values[buffer.readShort()];
    }
}
