package com.almostreliable.lib.registry;

import com.almostreliable.lib.util.AlmostUtils;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.Container;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;

import java.util.function.Supplier;

public class RecipeEntry<R extends Recipe<Container>> extends RegistryEntryImpl<RecipeSerializer<R>> implements RecipeType<R> {

    public RecipeEntry(ResourceLocation id, Supplier<RecipeSerializer<R>> supplier) {
        super(AlmostUtils.cast(Registry.RECIPE_SERIALIZER), id, supplier);
    }

    @Override
    public String toString() {
        return getId().toString();
    }
}
