package com.github.almostreliable.lib.mixin;

import com.github.almostreliable.lib.AlmostGameTest;
import net.minecraftforge.gametest.ForgeGameTestHooks;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.lang.reflect.Method;

/**
 * if {@link AlmostGameTest} is given we will it for the namespace
 */
@Mixin(ForgeGameTestHooks.class)
public class ForgeGameTestHooksMixin {

    @Inject(method = "getTemplateNamespace", at = @At(value = "RETURN"), remap = false, cancellable = true)
    private static void almostlib$getTemplateNamespace(Method method, CallbackInfoReturnable<String> cir) {
        AlmostGameTest almostGameTest = method.getDeclaringClass().getAnnotation(AlmostGameTest.class);
        if (almostGameTest != null) {
            cir.setReturnValue(almostGameTest.value());
        }
    }
}
