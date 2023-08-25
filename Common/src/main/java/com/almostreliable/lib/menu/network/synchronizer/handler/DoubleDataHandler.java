package com.almostreliable.lib.menu.network.synchronizer.handler;

import com.almostreliable.lib.menu.network.synchronizer.AbstractDataHandler;
import net.minecraft.network.FriendlyByteBuf;

import java.util.function.Consumer;
import java.util.function.Supplier;

public class DoubleDataHandler extends AbstractDataHandler<Double> {

    protected DoubleDataHandler(Supplier<Double> getter, Consumer<Double> setter) {
        super(getter, setter);
    }

    @Override
    protected void handleEncoding(FriendlyByteBuf buffer, Double value) {
        buffer.writeDouble(value);
    }

    @Override
    protected Double handleDecoding(FriendlyByteBuf buffer) {
        return buffer.readDouble();
    }
}
