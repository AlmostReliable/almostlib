package com.almostreliable.almostlib.menu.synchronizer.handler;

import com.almostreliable.almostlib.menu.synchronizer.AbstractDataHandler;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.item.ItemStack;

import java.util.function.Consumer;
import java.util.function.Supplier;

public class ItemStackDataHandler extends AbstractDataHandler<ItemStack> {

    protected ItemStackDataHandler(Supplier<ItemStack> getter, Consumer<ItemStack> setter) {
        super(getter, setter);
    }

    @Override
    protected void handleEncoding(FriendlyByteBuf buffer, ItemStack value) {
        buffer.writeItem(value);
    }

    @Override
    protected ItemStack handleDecoding(FriendlyByteBuf buffer) {
        return buffer.readItem();
    }
}
