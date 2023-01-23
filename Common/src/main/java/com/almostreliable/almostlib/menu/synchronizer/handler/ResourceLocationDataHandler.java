package com.almostreliable.almostlib.menu.synchronizer.handler;

import com.almostreliable.almostlib.menu.synchronizer.AbstractDataHandler;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;

import javax.annotation.Nullable;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class ResourceLocationDataHandler extends AbstractDataHandler<ResourceLocation> {

    protected ResourceLocationDataHandler(Supplier<ResourceLocation> getter, Consumer<ResourceLocation> setter) {
        super(getter, setter);
    }

    @Override
    protected void handleEncoding(FriendlyByteBuf buffer, @Nullable ResourceLocation value) {
        if (value == null) {
            buffer.writeBoolean(false);
        } else {
            buffer.writeBoolean(true);
            buffer.writeResourceLocation(value);
        }
    }

    @Nullable
    @Override
    protected ResourceLocation handleDecoding(FriendlyByteBuf buffer) {
        if (buffer.readBoolean()) {
            return buffer.readResourceLocation();
        }
        return null;
    }
}
