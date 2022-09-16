package com.almostreliable.almostlib.kubejs;

import com.almostreliable.almostlib.util.WeightedList;
import dev.latvian.mods.kubejs.KubeJSPlugin;
import dev.latvian.mods.kubejs.script.BindingsEvent;
import dev.latvian.mods.kubejs.script.ScriptType;
import dev.latvian.mods.rhino.util.wrap.TypeWrappers;
import net.minecraft.advancements.critereon.MinMaxBounds;

import java.util.Comparator;
import java.util.Optional;
import java.util.stream.Collectors;

public class Plugin extends KubeJSPlugin {

    @Override
    public void addBindings(BindingsEvent event) {
        event.add("AlmostJS", Bindings.class);

        // Some java bindings
        event.add("Optional", Optional.class);
        event.add("Collectors", Collectors.class);
        event.add("Comparator", Comparator.class);
    }

    @Override
    public void addTypeWrappers(ScriptType type, TypeWrappers typeWrappers) {
        typeWrappers.register(WeightedList.class, AlmostWrappers::ofWeightedList);
        typeWrappers.register(MinMaxBounds.Doubles.class, AlmostWrappers::ofDoubles);
        typeWrappers.register(MinMaxBounds.Ints.class, AlmostWrappers::ofInt);
    }
}
