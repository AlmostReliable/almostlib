package com.github.almostreliable.lib.mixin;

import com.github.almostreliable.lib.AlmostGameTest;
import com.github.almostreliable.lib.Utils;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestRegistry;
import net.minecraft.gametest.framework.StructureUtils;
import net.minecraft.gametest.framework.TestFunction;
import net.minecraft.world.level.block.Rotation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.lang.reflect.Method;
import java.util.function.Consumer;

@Mixin(GameTestRegistry.class)
public abstract class GameTestRegistryMixin {

    @Shadow
    private static Consumer<?> turnMethodIntoConsumer(Method $$0) {
        return null;
    }


    /**
     * If {@link AlmostGameTest} annotation exist, we will use the vanilla way to create the test function instead of
     */
    @Inject(method = "turnMethodIntoTestFunction", at = @At(value = "HEAD"), cancellable = true)
    private static void almostlib$transformTestFunction(Method method, CallbackInfoReturnable<TestFunction> cir) {
        GameTest gameTest = method.getAnnotation(GameTest.class);
        AlmostGameTest almostGameTest = method.getDeclaringClass().getAnnotation(AlmostGameTest.class);

        if (almostGameTest != null && gameTest != null) {
            String className = method.getDeclaringClass().getSimpleName().toLowerCase();
            String testName = className + "." + method.getName().toLowerCase();
            String templateStructure = gameTest.template().isEmpty()
                                       ? almostGameTest.value() + ":" + testName
                                       : gameTest.template();
            Rotation rotation = StructureUtils.getRotationForRotationSteps(gameTest.rotationSteps());
            TestFunction testFunction = new TestFunction(
                    gameTest.batch(),
                    testName,
                    templateStructure,
                    rotation,
                    gameTest.timeoutTicks(),
                    gameTest.setupTicks(),
                    gameTest.required(),
                    gameTest.requiredSuccesses(),
                    gameTest.attempts(),
                    Utils.cast(turnMethodIntoConsumer(method)));
            cir.setReturnValue(testFunction);
        }
    }
}
