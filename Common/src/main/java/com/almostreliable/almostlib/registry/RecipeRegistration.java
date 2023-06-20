package com.almostreliable.almostlib.registry;

import com.almostreliable.almostlib.util.AlmostUtils;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;

import java.util.function.Supplier;

public class RecipeRegistration extends Registration<RecipeSerializer<?>, RecipeEntry<? extends RecipeSerializer<?>>> {

    public RecipeRegistration(String namespace) {
        super(namespace, Registry.RECIPE_SERIALIZER);
    }

    @Override
    protected RecipeEntry<? extends RecipeSerializer<?>> createEntry(ResourceLocation id, Supplier<? extends RecipeSerializer<?>> supplier) {
        return new RecipeEntry<>(id, AlmostUtils.cast(supplier));
    }

    public <R extends RecipeSerializer<?>> RecipeEntry<R> register(String id, Supplier<? extends R> supplier) {
        return AlmostUtils.cast(createOrThrowEntry(id, supplier));
    }
}
