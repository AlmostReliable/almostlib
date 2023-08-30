package com.almostreliable.lib.mixin.config;

import com.almostreliable.lib.config.ConfigManager;
import net.minecraft.client.Minecraft;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.concurrent.CompletableFuture;

@Mixin(Minecraft.class)
public abstract class MinecraftMixin {

    @Shadow @Nullable
    private CompletableFuture<Void> pendingReload;

    @Inject(method = "reloadResourcePacks(Z)Ljava/util/concurrent/CompletableFuture;", at = @At("HEAD"))
    private void almostlib$onReloadClient(boolean bl, CallbackInfoReturnable<CompletableFuture<Void>> cir) {
        if (pendingReload == null) {
            ConfigManager.ReloadHandler.reloadClient();
        }
    }
}
