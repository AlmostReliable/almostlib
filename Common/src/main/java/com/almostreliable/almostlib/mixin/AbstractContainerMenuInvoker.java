package com.almostreliable.almostlib.mixin;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.level.block.Block;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(AbstractContainerMenuInvoker.class)
public interface AbstractContainerMenuInvoker {
    @Invoker("stillValid")
    static boolean stillValid(ContainerLevelAccess cla, Player player, Block block) {
        throw new AssertionError();
    }
}
