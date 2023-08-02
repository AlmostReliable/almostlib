package com.almostreliable.almostlib.gametest;

import com.almostreliable.almostlib.AlmostLib;
import com.almostreliable.almostlib.BuildConfig;
import com.almostreliable.almostlib.mixin.GameTestRegistryAccessor;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.gametest.framework.StructureUtils;
import net.minecraft.gametest.framework.TestFunction;
import net.minecraft.world.level.block.Rotation;

import javax.annotation.Nullable;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class GameTestLoader {

    public static final String ALMOSTLIB_GAMETEST_TEST_PACKAGES = "almostlib.gametest.testPackages";
    @Nullable
    private static List<Pattern> ENABLED_MODS;

    public static void registerProvider(GameTestProvider provider) {
        for (Method method : provider.getClass().getDeclaredMethods()) {
            GameTest gametest = method.getAnnotation(GameTest.class);
            if (gametest != null) {
                register(provider, method, gametest);
            }
        }
    }

    @SafeVarargs
    public static void registerProviders(Class<? extends GameTestProvider>... providerClasses) {
        for (Class<? extends GameTestProvider> providerClass : providerClasses) {
            try {
                var ctor = providerClass.getConstructor();
                var instance = ctor.newInstance();
                if (isAllowedModIdToRun(instance)) {
                    registerProvider(instance);
                }
            } catch (InvocationTargetException | NoSuchMethodException | InstantiationException | IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private static void register(GameTestProvider provider, Method method, GameTest gametest) {
        String simpleClassName = method.getDeclaringClass().getSimpleName();
        String simpleClassNameLowered = simpleClassName.toLowerCase();
        String methodName = method.getName().toLowerCase();

        String template = gametest.template();
        if (template.isEmpty()) {
            template = BuildConfig.MOD_ID + ":empty_test_structure";
        }

        Rotation rotation = StructureUtils.getRotationForRotationSteps(gametest.rotationSteps());

        var test = new TestFunction(
            gametest.batch(),
            simpleClassNameLowered + "." + methodName,
            template,
            rotation,
            gametest.timeoutTicks(),
            gametest.setupTicks(),
            gametest.required(),
            gametest.requiredSuccesses(),
            gametest.attempts(),
            turnMethodIntoConsumer(provider, method)
        );

        GameTestRegistryAccessor.TEST_FUNCTIONS().add(test);
        GameTestRegistryAccessor.TEST_CLASS_NAMES().add(simpleClassName);
    }

    private static Consumer<GameTestHelper> turnMethodIntoConsumer(GameTestProvider provider, Method method) {
        if (Modifier.isStatic(method.getModifiers())) {
            throw new RuntimeException("Static methods are not supported");
        }

        return testHelper -> {
            try {
                method.invoke(provider, testHelper);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        };
    }

    private static boolean isAllowedModIdToRun(GameTestProvider provider) {
        if (ENABLED_MODS == null) {
            String enabledNamespacesStr = System.getProperty(ALMOSTLIB_GAMETEST_TEST_PACKAGES);
            if (enabledNamespacesStr == null) {
                ENABLED_MODS = Collections.emptyList();
            } else {
                ENABLED_MODS = Arrays.stream(enabledNamespacesStr.split(","))
                    .map(String::trim)
                    .filter(s -> !s.isEmpty())
                    .map(Pattern::compile)
                    .toList();
                AlmostLib.LOGGER.info("Enabled gametests for mods: " + ENABLED_MODS);
            }
        }

        String name = provider.getClass().getName();
        return ENABLED_MODS.stream().map(p -> p.matcher(name)).anyMatch(Matcher::matches);
    }
}
