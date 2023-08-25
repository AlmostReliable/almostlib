package com.almostreliable.lib.menu.network.synchronizer.handler;

import com.almostreliable.lib.menu.network.synchronizer.AbstractDataHandler;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;

import java.util.function.Consumer;
import java.util.function.Supplier;

public class ResourceLocationDataHandler extends AbstractDataHandler<ResourceLocation> {

    protected ResourceLocationDataHandler(Supplier<ResourceLocation> getter, Consumer<ResourceLocation> setter) {
        super(getter, setter);
    }

    @Override
    protected void handleEncoding(FriendlyByteBuf buffer, ResourceLocation value) {
        buffer.writeResourceLocation(value);
    }

    @Override
    protected ResourceLocation handleDecoding(FriendlyByteBuf buffer) {
        return buffer.readResourceLocation();
    }
}
