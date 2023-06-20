package com.almostreliable.almostlib.registry;

import com.almostreliable.almostlib.util.AlmostUtils;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;

import java.util.function.Supplier;

public class RecipeEntry<R extends RecipeSerializer<? extends Recipe<?>>> extends RegistryEntryImpl<R> implements RecipeType<Recipe<?>> {

    public RecipeEntry(ResourceLocation id, Supplier<R> supplier) {
        super(AlmostUtils.cast(Registry.RECIPE_SERIALIZER), id, supplier);
    }

    @Override
    public String toString() {
        return getId().toString();
    }
}
