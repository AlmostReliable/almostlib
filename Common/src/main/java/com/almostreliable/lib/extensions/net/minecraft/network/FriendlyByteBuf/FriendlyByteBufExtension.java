package com.almostreliable.lib.extensions.net.minecraft.network.FriendlyByteBuf;

import manifold.ext.rt.api.Extension;
import manifold.ext.rt.api.This;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;

@Extension
public final class FriendlyByteBufExtension {

    private FriendlyByteBufExtension() {}

    public static CompoundTag readOrCreateNbt(@This FriendlyByteBuf thiz) {
        CompoundTag nbt = thiz.readNbt();
        if (nbt == null) return new CompoundTag();
        return nbt;
    }
}
