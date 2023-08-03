package com.almostreliable.almostlib.component;

import net.minecraft.core.Direction;
import net.minecraft.world.Container;
import net.minecraft.world.level.block.entity.BlockEntity;

import javax.annotation.Nullable;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Consumer;

/**
 * Implemented by {@link BlockEntity}s that expose at least one resource container.
 * <p>
 * After implementation, return the specific containers in their respective getters, so
 * they are exposed to the environment.
 * <p>
 * {@link BlockEntity}s implementing this interface are automatically registered to the
 * respective sub-systems in the loader.
 * <p>
 * In order to support Forge in the most optimal way, make sure to store the invalidation
 * listener consumer and pass it pack to the getter. This way, the container can be invalidated
 * for all Forge capability listeners when one of the invalidation methods is called.<br>
 * A good place to invalidate the container is the {@link BlockEntity#setRemoved()} method.
 */
public interface ComponentHolder {

    /**
     * Gets the item container for the given side.
     * <p>
     * If the {@link BlockEntity} doesn't have an item container or doesn't
     * expose it on the given side, return null.
     *
     * @param side The side to get the container for.
     * @return The item container or null if not available.
     */
    @Nullable
    default Container getItemContainer(@Nullable Direction side) {
        return null;
    }

    /**
     * Gets the energy container for the given side.
     * <p>
     * If the {@link BlockEntity} doesn't have an energy container or doesn't
     * expose it on the given side, return null.
     *
     * @param side The side to get the container for.
     * @return The energy container or null if not available.
     */
    @Nullable
    default EnergyContainer getEnergyContainer(@Nullable Direction side) {
        return null;
    }

    /**
     * Adds the invalidation listener for all containers.
     * <p>
     * Store this locally inside the {@link BlockEntity} and pass it back
     * to the {@link #getInvalidateListener()} method.
     *
     * @param listener The listener to add.
     */
    void addInvalidateListener(Consumer<Object> listener);

    /**
     * Gets the invalidation listener for all containers.
     * <p>
     * This is used to invalidate all containers when one of the invalidation
     * methods is called.
     * <p>
     * The instance of the listener is passed to this {@link BlockEntity} by
     * {@link #addInvalidateListener(Consumer)}.
     *
     * @return The invalidation listener or null if not available.
     */
    @Nullable
    Consumer<Object> getInvalidateListener();

    /**
     * Invalidates the given container.
     * <p>
     * This notifies all attached listeners on the Forge side.
     *
     * @param container The container to invalidate.
     */
    default void invalidateContainer(@Nullable Object container) {
        Consumer<Object> l = getInvalidateListener();
        if (l != null) {
            l.accept(container);
        }
    }

    /**
     * Invalidates the item container for the given side.
     * <p>
     * This notifies all attached listeners on the Forge side.
     *
     * @param side The side to invalidate the container for.
     */
    default void invalidateItemContainer(@Nullable Direction side) {
        var container = getItemContainer(side);
        if (container != null) {
            invalidateContainer(container);
        }
    }

    /**
     * Invalidates all item containers.
     * <p>
     * This notifies all attached listeners on the Forge side.
     */
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

    /**
     * Invalidates the energy container for the given side.
     * <p>
     * This notifies all attached listeners on the Forge side.
     *
     * @param side The side to invalidate the container for.
     */
    default void invalidateEnergyContainer(@Nullable Direction side) {
        var container = getEnergyContainer(side);
        if (container != null) {
            invalidateContainer(container);
        }
    }

    /**
     * Invalidates all energy containers.
     * <p>
     * This notifies all attached listeners on the Forge side.
     */
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
