package com.almostreliable.lib.mixin;

import net.minecraft.gametest.framework.GameTestRegistry;
import net.minecraft.gametest.framework.TestFunction;
import net.minecraft.server.level.ServerLevel;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;

@Mixin(GameTestRegistry.class)
public interface GameTestRegistryAccessor {

    @Accessor("BEFORE_BATCH_FUNCTIONS")
    static Map<String, Consumer<ServerLevel>> almostLib$getBeforeBatches() {
        throw new AssertionError();
    }

    @Accessor("AFTER_BATCH_FUNCTIONS")
    static Map<String, Consumer<ServerLevel>> almostLib$getAfterBatches() {
        throw new AssertionError();
    }

    @Invoker("registerBatchFunction")
    static <T extends Annotation> void almostLib$registerBatchFunction(Method method, Class<T> clazz, Function<T, String> function, Map<String, Consumer<ServerLevel>> map) {
        throw new AssertionError();
    }

    @Invoker("useTestGeneratorMethod")
    static Collection<TestFunction> almostlib$useTestGeneratorMethod(Method method) {
        throw new AssertionError();
    }

    @Invoker("turnMethodIntoConsumer")
    static Consumer<?> almostlib$turnMethodIntoConsumer(Method method) {
        throw new AssertionError();
    }

}
