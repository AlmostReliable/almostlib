package com.almostreliable.lib.datagen.recipe;

import com.almostreliable.lib.item.IngredientStack;
import com.almostreliable.lib.util.JsonSupplier;
import com.google.gson.JsonObject;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;

/**
 * An abstraction for an item or an ingredient inside a datagen recipe.
 * <p>
 * Can be used to add all kinds of item or ingredient components to {@link FinishedRecipe}s.
 */
public interface RecipeComponent {

    static JsonSupplier of(ResourceLocation id, int count) {
        JsonObject json = new JsonObject();
        json.addProperty("item", id.toString());
        if (count > 1) json.addProperty("count", count);
        return () -> json;
    }

    static JsonSupplier of(ResourceLocation id) {
        return of(id, 1);
    }

    static JsonSupplier of(String rawId, int count) {
        if (rawId.startsWith("#")) {
            JsonObject json = new JsonObject();
            json.addProperty("tag", rawId.substring(1));
            if (count > 1) json.addProperty("count", count);
            return () -> json;
        }
        return of(new ResourceLocation(rawId));
    }

    static JsonSupplier of(String rawId) {
        return of(rawId, 1);
    }

    static JsonSupplier of(JsonObject json) {
        return () -> json;
    }

    static JsonSupplier of(Ingredient ingredient, int count) {
        return IngredientStack.of(ingredient, count);
    }

    static JsonSupplier of(Ingredient ingredient) {
        return IngredientStack.of(ingredient);
    }

    static JsonSupplier of(ItemStack stack, int count) {
        return IngredientStack.of(stack, count);
    }

    static JsonSupplier of(ItemStack stack) {
        return IngredientStack.of(stack);
    }

    static JsonSupplier of(ItemLike itemLike, int count) {
        return IngredientStack.of(itemLike, count);
    }

    static JsonSupplier of(ItemLike itemLike) {
        return IngredientStack.of(itemLike);
    }

    static JsonSupplier of(TagKey<Item> itemTag, int count) {
        return IngredientStack.of(itemTag, count);
    }

    static JsonSupplier of(TagKey<Item> itemTag) {
        return IngredientStack.of(itemTag);
    }
}
