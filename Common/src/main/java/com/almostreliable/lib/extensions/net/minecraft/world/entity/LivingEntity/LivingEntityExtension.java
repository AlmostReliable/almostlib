package com.almostreliable.lib.extensions.net.minecraft.world.entity.LivingEntity;

import manifold.ext.rt.api.Extension;
import manifold.ext.rt.api.This;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

@Extension
public final class LivingEntityExtension {

    private LivingEntityExtension() {}

    public static void setMainHandItem(@This LivingEntity thiz, ItemStack stack) {
        thiz.setItemInHand(InteractionHand.MAIN_HAND, stack);
    }
}
