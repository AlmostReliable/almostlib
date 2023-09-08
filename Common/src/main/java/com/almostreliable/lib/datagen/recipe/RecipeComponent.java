package com.almostreliable.lib.datagen.recipe;

import com.almostreliable.lib.item.IngredientStack;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;

import java.util.Set;

/**
 * An abstraction for an item or an ingredient inside a datagen recipe.
 * <p>
 * Can be used to add all kinds of item or ingredient components to {@link FinishedRecipe}s.
 */
public interface RecipeComponent {

    static RecipeComponent of(ResourceLocation id, int count) {
        return new RecipeComponent() {
            @Override
            public JsonElement toJson() {
                JsonObject json = new JsonObject();
                json.addProperty("item", id.toString());
                if (count > 1) json.addProperty("count", count);
                return json;
            }

            @Override
            public Set<String> getModIds() {
                return Set.of(id.getNamespace());
            }
        };
    }

    static RecipeComponent of(ResourceLocation id) {
        return of(id, 1);
    }

    static RecipeComponent of(String rawId, int count) {
        if (!rawId.startsWith("#")) return of(new ResourceLocation(rawId), count);

        return new RecipeComponent() {
            @Override
            public JsonElement toJson() {
                JsonObject json = new JsonObject();
                json.addProperty("tag", rawId.substring(1));
                if (count > 1) json.addProperty("count", count);
                return json;
            }

            @Override
            public Set<String> getModIds() {
                return Set.of(rawId.substring(1, rawId.indexOf(':')));
            }
        };
    }

    static RecipeComponent of(String rawId) {
        return of(rawId, 1);
    }

    static RecipeComponent of(Ingredient ingredient, int count) {
        return IngredientStack.of(ingredient, count);
    }

    static RecipeComponent of(Ingredient ingredient) {
        return IngredientStack.of(ingredient);
    }

    static RecipeComponent of(ItemStack stack, int count) {
        return IngredientStack.of(stack, count);
    }

    static RecipeComponent of(ItemStack stack) {
        return IngredientStack.of(stack);
    }

    static RecipeComponent of(ItemLike itemLike, int count) {
        return IngredientStack.of(itemLike, count);
    }

    static RecipeComponent of(ItemLike itemLike) {
        return IngredientStack.of(itemLike);
    }

    static RecipeComponent of(TagKey<Item> itemTag, int count) {
        return IngredientStack.of(itemTag, count);
    }

    static RecipeComponent of(TagKey<Item> itemTag) {
        return IngredientStack.of(itemTag);
    }

    JsonElement toJson();

    Set<String> getModIds();
}
