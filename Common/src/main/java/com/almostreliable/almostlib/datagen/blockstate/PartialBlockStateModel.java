package com.almostreliable.almostlib.datagen.blockstate;

import com.almostreliable.almostlib.util.AlmostUtils;
import com.almostreliable.almostlib.util.Rotation;
import com.google.common.collect.Maps;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;

import java.util.Comparator;
import java.util.Map;
import java.util.Objects;
import java.util.SortedMap;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class PartialBlockStateModel implements Supplier<JsonElement>, Predicate<BlockState> {

    private final SortedMap<Property<?>, Comparable<?>> filteredStates;
    private final BlockState state;
    private ResourceLocation model;
    private Rotation xRotation = Rotation.R0;
    private Rotation yRotation = Rotation.R0;
    private boolean uvLock;

    private PartialBlockStateModel(BlockState state, SortedMap<Property<?>, Comparable<?>> filteredStates) {
        this.filteredStates = filteredStates;
        this.state = state;
    }

    public static PartialBlockStateModel create(BlockState state, Predicate<Property<?>> propertyFilter) {
        SortedMap<Property<?>, Comparable<?>> properties = Maps.newTreeMap(Comparator.comparing(Property::getName));
        state.getProperties().stream().filter(propertyFilter).forEach((p) -> {
            properties.put(p, state.getValue(p));
        });
        return new PartialBlockStateModel(state, properties);
    }

    public BlockState getBlockState() {
        return state;
    }

    public <T extends Comparable<T>> T getValue(Property<T> property) {
        return AlmostUtils.cast(Objects.requireNonNull(filteredStates.get(property), "Property " + property + " does not exist in " + filteredStates));
    }

    protected String propertiesToString() {
        return filteredStates.entrySet().stream().map(e -> e.getKey().getName() + "=" + e.getValue()).collect(Collectors.joining(","));
    }

    @Override
    public JsonElement get() {
        JsonObject json = new JsonObject();
        json.addProperty("model", model.toString());

        if (xRotation.value != 0) {
            json.addProperty("x", xRotation.value);
        }

        if (yRotation.value != 0) {
            json.addProperty("y", yRotation.value);
        }

        if (uvLock) {
            json.addProperty("uvlock", true);
        }
        return json;
    }

    public PartialBlockStateModel model(ResourceLocation texturePath) {
        this.model = texturePath;
        return this;
    }

    public PartialBlockStateModel xRotation(Rotation rotation) {
        this.xRotation = rotation;
        return this;
    }

    public PartialBlockStateModel yRotation(Rotation rotation) {
        this.yRotation = rotation;
        return this;
    }

    public PartialBlockStateModel uvLock(boolean uvLock) {
        this.uvLock = uvLock;
        return this;
    }

    @Override
    public int hashCode() {
        return Objects.hash(model, xRotation, yRotation, uvLock);
    }

    @Override
    public boolean test(BlockState blockState) {
        for (Map.Entry<Property<?>, Comparable<?>> e : filteredStates.entrySet()) {
            Property<?> property = e.getKey();
            if (blockState.hasProperty(property) && !blockState.getValue(property).equals(e.getValue())) {
                return false;
            }
        }
        return true;
    }
}
