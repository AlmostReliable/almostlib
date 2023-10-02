package com.almostreliable.lib.menu.network.synchronizer.handler;

import com.almostreliable.lib.menu.network.synchronizer.AbstractDataHandler;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.item.ItemStack;

import java.util.function.Consumer;
import java.util.function.Supplier;

public class ItemStackDataHandler extends AbstractDataHandler<ItemStack> {

    public ItemStackDataHandler(Supplier<ItemStack> getter, Consumer<ItemStack> setter) {
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
