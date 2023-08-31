package com.almostreliable.lib.hook;

import java.util.ArrayList;
import java.util.List;

public final class RecipeManagerHooks {

    private static final List<Runnable> ON_RECIPE_RELOAD = new ArrayList<>();
    private static final List<Runnable> POST_RECIPE_RELOAD = new ArrayList<>();

    private RecipeManagerHooks() {}

    /**
     * Register a runnable to be executed before recipes are reloaded.
     * <p>
     * Useful for clearing caches that are filled by recipe serializers.
     *
     * @param runnable The runnable to execute.
     */
    public static void onRecipeReloadBefore(Runnable runnable) {
        ON_RECIPE_RELOAD.add(runnable);
    }

    /**
     * Register a runnable to be executed after recipes are reloaded.
     *
     * @param runnable The runnable to execute.
     */
    public static void onRecipeReloadAfter(Runnable runnable) {
        POST_RECIPE_RELOAD.add(runnable);
    }

    public static void execRecipeReloadBefore() {
        ON_RECIPE_RELOAD.forEach(Runnable::run);
    }

    public static void execRecipeReloadAfter() {
        POST_RECIPE_RELOAD.forEach(Runnable::run);
    }
}
