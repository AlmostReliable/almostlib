package com.almostreliable.almostlib.item;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.crafting.Ingredient;

public record IngredientStack(Ingredient ingredient, int count) {

    public static IngredientStack fromJson(JsonElement json) {
        if (json instanceof JsonObject jsonObject && jsonObject.has("count")) {
            var ingredient = Ingredient.fromJson(jsonObject.get("ingredient"));
            var count = GsonHelper.getAsInt(jsonObject, "count", 1);
            return new IngredientStack(ingredient, count);
        }
        return new IngredientStack(Ingredient.fromJson(json), 1);
    }

    public JsonElement toJson() {
        var json = new JsonObject();
        var ingredientJson = ingredient.toJson();
        json.add("ingredient", ingredientJson);
        if (count > 1) {
            json.addProperty("count", count);
        }
        return json;
    }

    public static IngredientStack fromNetwork(FriendlyByteBuf buffer) {
        var ingredient = Ingredient.fromNetwork(buffer);
        var count = buffer.readVarInt();
        return new IngredientStack(ingredient, count);
    }

    public void toNetwork(FriendlyByteBuf buffer) {
        ingredient.toNetwork(buffer);
        buffer.writeVarInt(count);
    }
}
