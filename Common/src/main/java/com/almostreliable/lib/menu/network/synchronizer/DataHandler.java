package com.almostreliable.lib.menu.network.synchronizer;

import net.minecraft.network.FriendlyByteBuf;

/**
 * An interface for handling menu data synchronization.
 * <p>
 * This can be implemented by any class that needs custom data synchronization.
 * For primitives and basic types, the {@link AbstractDataHandler} class implementations can be used.
 */
public interface DataHandler {

    /**
     * Encodes the data to the buffer.
     *
     * @param buffer The buffer to encode to
     */
    void encode(FriendlyByteBuf buffer);

    /**
     * Decodes the data from the buffer.
     *
     * @param buffer The buffer to decode from
     */
    void decode(FriendlyByteBuf buffer);

    /**
     * Checks if the data has changed since the last synchronization.
     *
     * @return True if the data has changed, false otherwise
     */
    boolean hasChanged();
}
