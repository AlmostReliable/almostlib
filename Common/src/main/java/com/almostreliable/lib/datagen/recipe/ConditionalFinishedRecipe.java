package com.almostreliable.lib.datagen.recipe;

import com.almostreliable.lib.AlmostLib;
import com.google.gson.JsonObject;
import net.minecraft.data.recipes.FinishedRecipe;

import java.util.HashSet;
import java.util.Set;

/**
 * A {@link FinishedRecipe} used to automatically add mod-loaded conditions to
 * serialized recipes.
 */
public interface ConditionalFinishedRecipe extends FinishedRecipe {

    @Override
    default JsonObject serializeRecipe() {
        Set<String> modIds = new HashSet<>(getOptionalModIds());
        modIds.removeAll(Set.of("minecraft", "fabric", "forge", getId().getNamespace()));

        JsonObject json = FinishedRecipe.super.serializeRecipe();
        if (!modIds.isEmpty()) {
            AlmostLib.PLATFORM.writeRecipeModConditions(json, modIds);
        }
        return json;
    }

    Set<String> getOptionalModIds();
}
