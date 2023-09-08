package com.almostreliable.lib.datagen.recipe;

import com.almostreliable.lib.AlmostLib;
import com.google.gson.JsonObject;
import net.minecraft.data.recipes.FinishedRecipe;

import java.util.ArrayList;
import java.util.List;

/**
 * A {@link FinishedRecipe} used to automatically add mod-loaded conditions to
 * serialized recipes.
 */
public interface ConditionalFinishedRecipe extends FinishedRecipe {

    @Override
    default JsonObject serializeRecipe() {
        var modIds = new ArrayList<>(getOptionalModIds());
        modIds.removeAll(List.of("minecraft", "fabric", "forge", getId().getNamespace()));

        JsonObject json = FinishedRecipe.super.serializeRecipe();
        if (!modIds.isEmpty()) {
            AlmostLib.PLATFORM.writeRecipeModConditions(json, modIds);
        }
        return json;
    }

    List<String> getOptionalModIds();
}
