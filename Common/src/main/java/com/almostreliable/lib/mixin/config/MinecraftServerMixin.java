package com.almostreliable.lib.mixin.config;

import com.almostreliable.lib.config.ConfigManager;
import net.minecraft.server.MinecraftServer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Collection;
import java.util.concurrent.CompletableFuture;

@Mixin(MinecraftServer.class)
public abstract class MinecraftServerMixin {

    @Inject(method = "reloadResources", at = @At("HEAD"))
    private void almostlib$onReloadClient(Collection<String> selectedIds, CallbackInfoReturnable<CompletableFuture<Void>> cir) {
        ConfigManager.ReloadHandler.reloadServer();
    }
}
