package com.almostreliable.almostlib.component;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import javax.annotation.Nullable;

public interface ItemView {

    /**
     * Returns the item stored in this view.
     *
     * @return The item stored in this view.
     */
    Item getItem();

    /**
     * Returns the amount of the item stored in this view.
     *
     * @return The amount of the item stored in this view.
     */
    long getAmount();

    /**
     * Returns the NBT data of the item stored in this view.
     * <p>
     * May be null if the item has no NBT data.
     *
     * @return The NBT data of the item stored in this view.
     */
    @Nullable
    CompoundTag getNbt();

    /**
     * Returns whether this view allows insertion.
     *
     * @return True if this view allows insertion, false otherwise.
     */
    boolean allowsInsertion();

    /**
     * Returns whether this view allows extraction.
     *
     * @return True if this view allows extraction, false otherwise.
     */
    boolean allowsExtraction();

    /**
     * Inserts the given amount of items matching the filter into this view.
     * <p>
     * Keep in mind that the {@link ItemStack} is only acting as a filter to allow for
     * item type and NBT matching. The count of the {@link ItemStack} is completely ignored.<br>
     * Instead, the amount parameter is used to determine how many items to insert.
     *
     * @param filter   The filter to match against.
     * @param amount   The amount of items to insert.
     * @param simulate Whether to simulate the insertion.
     * @return The amount of items that were actually inserted.
     */
    long insert(ItemStack filter, long amount, boolean simulate);

    /**
     * Inserts the given amount of items into this view.
     * <p>
     * This method uses the existing item of the view as the filter to insert.<br>
     * If you want to define a custom filter, use {@link #insert(ItemStack, long, boolean)}.
     *
     * @param amount   The amount of items to insert.
     * @param simulate Whether to simulate the insertion.
     * @return The amount of items that were actually inserted.
     */
    long insert(long amount, boolean simulate);

    /**
     * Inserts the given items into this view.
     * <p>
     * This method uses the count of the filter as the amount to insert.<br>
     * If you want to define a custom amount, use {@link #insert(ItemStack, long, boolean)}.
     *
     * @param filter   The filter to match against.
     * @param simulate Whether to simulate the insertion.
     * @return The amount of items that were actually inserted.
     */
    default long insert(ItemStack filter, boolean simulate) {
        return insert(filter, filter.getCount(), simulate);
    }

    /**
     * Extracts the given amount of items from this view.
     * <p>
     * This method uses the existing item of the view as the filter to extract.
     *
     * @param amount   The amount of items to extract.
     * @param simulate Whether to simulate the extraction.
     * @return The amount of items that were actually extracted.
     */
    long extract(long amount, boolean simulate);

    /**
     * Clears this view.
     */
    void clear();
}
