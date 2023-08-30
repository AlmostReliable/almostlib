package com.almostreliable.lib.item;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.crafting.Ingredient;

import javax.annotation.Nullable;

/**
 * A class representing an ingredient with a count.
 * <p>
 * Can be used to have a recipe tag input with a specific count.
 * <p>
 * The serializer will create a {@link JsonObject} that holds the vanilla ingredient
 * in a nested element called "ingredient" while the "count" property will be placed
 * in the root of the object.<br>
 * This ensures covering custom ingredients that are serialized to a {@link JsonArray}.
 * <p>
 * The deserializer is also capable of reading vanilla ingredients.
 */
public class IngredientStack {

    public static final Serializer SERIALIZER = new Serializer();

    protected final Ingredient ingredient;
    protected final int count;

    protected IngredientStack(Ingredient ingredient, int count) {
        this.ingredient = ingredient;
        this.count = count;
    }

    /**
     * Creates a new IngredientStack with the given ingredient and count.
     *
     * @param ingredient The ingredient.
     * @param count      The count of the ingredient.
     */
    public static IngredientStack of(Ingredient ingredient, int count) {
        return new IngredientStack(ingredient, count);
    }

    /**
     * Creates a new IngredientStack with the given ingredient and a count of 1.
     *
     * @param ingredient The ingredient.
     */
    public static IngredientStack of(Ingredient ingredient) {
        return new IngredientStack(ingredient, 1);
    }

    public Ingredient getIngredient() {
        return ingredient;
    }

    public int getCount() {
        return count;
    }

    public static class Serializer {

        /**
         * Deserializes the given {@link IngredientStack} from the given {@link JsonElement}.
         * <p>
         * Capable of reading vanilla ingredients that are serialized as {@link JsonObject}
         * and custom ingredients that are serialized as {@link JsonArray}.
         *
         * @param json The json element to deserialize.
         * @return The IngredientStack.
         */
        public static IngredientStack fromJson(@Nullable JsonElement json) {
            if (json instanceof JsonObject jsonObject && jsonObject.has("ingredient")) {
                // nested ingredient found, serialized IngredientStack
                Ingredient ingredient = Ingredient.fromJson(jsonObject.get("ingredient"));
                int count = GsonHelper.getAsInt(jsonObject, "count", 1);
                return of(ingredient, count);
            }

            if (json instanceof JsonObject || json instanceof JsonArray) {
                // vanilla ingredient found, serialized Ingredient
                return of(Ingredient.fromJson(json));
            }

            throw new IllegalArgumentException("Expected either a JsonObject or a JsonArray");
        }

        /**
         * Deserializes the given {@link IngredientStack} from the given {@link FriendlyByteBuf}.
         *
         * @param buffer The buffer to read from.
         * @return The IngredientStack.
         */
        public static IngredientStack fromNetwork(FriendlyByteBuf buffer) {
            var ingredient = Ingredient.fromNetwork(buffer);
            var count = buffer.readVarInt();
            return new IngredientStack(ingredient, count);
        }

        /**
         * Serializes the given {@link IngredientStack} to a {@link JsonObject}.
         * <p>
         * Omits the "count" property if the count is 1.
         *
         * @param ingredientStack The ingredient stack to serialize.
         * @return The json object.
         */
        public JsonObject toJson(IngredientStack ingredientStack) {
            var json = new JsonObject();
            json.add("ingredient", ingredientStack.ingredient.toJson());
            if (ingredientStack.count > 1) {
                json.addProperty("count", ingredientStack.count);
            }
            return json;
        }

        /**
         * Serializes the given {@link IngredientStack} to the given {@link FriendlyByteBuf}.
         *
         * @param buffer The buffer to write to.
         * @param ingredientStack The ingredient stack to serialize.
         */
        public void toNetwork(FriendlyByteBuf buffer, IngredientStack ingredientStack) {
            ingredientStack.ingredient.toNetwork(buffer);
            buffer.writeVarInt(ingredientStack.count);
        }
    }
}
