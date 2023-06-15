package com.almostreliable.almostlib.component;

import net.minecraft.core.Direction;
import net.minecraft.world.Container;

import javax.annotation.Nullable;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Consumer;

public interface ComponentHolder {

    @Nullable
    default Container getItemContainer(@Nullable Direction side) {
        return null;
    }

    @Nullable
    default EnergyContainer getEnergyContainer(@Nullable Direction side) {
        return null;
    }

    void addInvalidateListener(Consumer<Object> listener);

    @Nullable
    Consumer<Object> getInvalidateListener();

    default void invalidateContainer(@Nullable Object container) {
        Consumer<Object> l = getInvalidateListener();
        if (l != null) {
            l.accept(container);
        }
    }

    default void invalidateItemContainer(@Nullable Direction side) {
        var container = getItemContainer(side);
        if (container != null) {
            invalidateContainer(container);
        }
    }

    default void invalidateItemContainers() {
        Set<Container> handled = new HashSet<>();
        for (Direction direction : Direction.values()) {
            var container = getItemContainer(direction);
            if (container != null && !handled.contains(container)) {
                handled.add(container);
                invalidateContainer(container);
            }
        }
    }

    default void invalidateEnergyContainer(@Nullable Direction side) {
        var container = getEnergyContainer(side);
        if (container != null) {
            invalidateContainer(container);
        }
    }

    default void invalidateEnergyContainers() {
        Set<EnergyContainer> handled = new HashSet<>();
        for (Direction direction : Direction.values()) {
            var container = getEnergyContainer(direction);
            if (container != null && !handled.contains(container)) {
                handled.add(container);
                invalidateContainer(container);
            }
        }
    }
}
