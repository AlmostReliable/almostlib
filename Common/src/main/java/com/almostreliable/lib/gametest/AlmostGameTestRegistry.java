package com.almostreliable.lib.gametest;

import com.almostreliable.lib.mixin.GameTestRegistryAccessor;
import net.minecraft.gametest.framework.*;

import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Locale;
import java.util.function.Consumer;

public class AlmostGameTestRegistry {

    public static void register(String className) {
        try {
            Class<?> clazz = Class.forName(className);
            register(clazz);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public static void register(Class<?> clazz) {
        for (Method declaredMethod : clazz.getDeclaredMethods()) {
            register(declaredMethod);
        }
    }

    protected static void register(Method method) {
        Class<?> owner = method.getDeclaringClass();
        String ownerName = owner.getSimpleName();
        GameTest gameTest = method.getAnnotation(GameTest.class);
        if (gameTest != null) {
            AlmostGameTest almostGameTest = owner.getAnnotation(AlmostGameTest.class);
            if (almostGameTest == null) {
                throw new IllegalArgumentException(
                        "Missing @AlmostGameTest annotation on " + owner.getName() + "::" + method.getName());
            }
            TestFunction testFunction = createTestFunction(gameTest, almostGameTest, method);
            GameTestRegistry.getAllTestFunctions().add(testFunction);
            GameTestRegistry.getAllTestClassNames().add(ownerName);
        }

        GameTestGenerator generator = method.getAnnotation(GameTestGenerator.class);
        if (generator != null) {
            Collection<TestFunction> testFunctions = GameTestRegistryAccessor.almostlib$useTestGeneratorMethod(method);
            GameTestRegistry.getAllTestFunctions().addAll(testFunctions);
            GameTestRegistry.getAllTestClassNames().add(ownerName);
        }

        GameTestRegistryAccessor.almostLib$registerBatchFunction(method,
                BeforeBatch.class,
                BeforeBatch::batch,
                GameTestRegistryAccessor.almostLib$getBeforeBatches());
        GameTestRegistryAccessor.almostLib$registerBatchFunction(method,
                AfterBatch.class,
                AfterBatch::batch,
                GameTestRegistryAccessor.almostLib$getAfterBatches());
    }

    protected static TestFunction createTestFunction(GameTest gameTest, AlmostGameTest almostGameTest, Method method) {
        String className = method.getDeclaringClass().getSimpleName().toLowerCase(Locale.ROOT);
        String testName = className + "." + method.getName().toLowerCase(Locale.ROOT);

        String templateStructure = gameTest.template().isEmpty()
                                   ? almostGameTest.value() + ":" + testName
                                   : gameTest.template();

        //noinspection unchecked
        return new TestFunction(
                gameTest.batch(),
                testName,
                templateStructure,
                StructureUtils.getRotationForRotationSteps(gameTest.rotationSteps()),
                gameTest.timeoutTicks(),
                gameTest.setupTicks(),
                gameTest.required(),
                gameTest.requiredSuccesses(),
                gameTest.attempts(),
                (Consumer<GameTestHelper>) GameTestRegistryAccessor.almostlib$turnMethodIntoConsumer(method));

    }
}
