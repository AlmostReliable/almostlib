package com.almostreliable.almostlib.util;

import com.almostreliable.almostlib.block.TickableEntityBlock;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;

/**
 * Implemented by objects that can be ticked.
 * <p>
 * <b>Implemented automatic tickers:</b>
 * <ul>
 *     <li>{@link BlockEntity}s hosted by a {@link TickableEntityBlock}</li>
 * </ul>
 */
public interface Tickable {

    /**
     * Handles the ticking for the given object.
     * @param level The level to determine if the object is on the client or server.
     * @param o The object to tick.
     */
    static void handleTick(Level level, Object o) {
        if (o instanceof Tickable tickable) {
            if (level.isClientSide) {
                tickable.clientTick();
            } else {
                tickable.serverTick();
            }
            tickable.tick();
        }
    }

    /**
     * Called on the client every tick.
     * <p>
     * If a common tick method is required, use {@link #tick()}.
     */
    default void clientTick() {}

    /**
     * Called on the server every tick.
     * <p>
     * If a common tick method is required, use {@link #tick()}.
     */
    default void serverTick() {}

    /**
     * Called on both the client and server every tick.
     * <p>
     * If a client-only or server-only tick method is required, use {@link #clientTick()} or {@link #serverTick()}.
     */
    default void tick() {}
}
