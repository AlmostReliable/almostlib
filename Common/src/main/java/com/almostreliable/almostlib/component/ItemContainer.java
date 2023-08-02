package com.almostreliable.almostlib.component;

import net.minecraft.world.item.ItemStack;

public interface ItemContainer extends Iterable<ItemView> {

    /**
     * Returns whether this container allows insertion of items.
     *
     * @return True if this container allows insertion, false otherwise.
     */
    boolean allowsInsertion();

    /**
     * Returns whether this container allows extraction of items.
     *
     * @return True if this container allows extraction, false otherwise.
     */
    boolean allowsExtraction();

    /**
     * Inserts the given amount of items matching the filter into this container.
     * <p>
     * Keep in mind that the {@link ItemStack} is only acting as a filter to allow for
     * item type and NBT matching. The count of the {@link ItemStack} is completely ignored.<br>
     * Instead, the amount parameter is used to determine how many items to insert.
     * <p>
     * Remember that the inserted amount can exceed the max stack size of the filter.<br>
     * If you don't want that, adjust the amount parameter accordingly.
     *
     * @param filter   The filter to match against.
     * @param amount   The amount of items to insert.
     * @param simulate Whether to simulate the insertion.
     * @return The amount of items that were actually or would have been inserted if simulated.
     */
    long insert(ItemStack filter, long amount, boolean simulate);

    /**
     * Inserts the given items into this container.
     * <p>
     * This method uses the count of the filter as the amount to insert.<br>
     * If you want to define a custom amount, use {@link #insert(ItemStack, long, boolean)}.
     *
     * @param filter   The filter to match against.
     * @param simulate Whether to simulate the insertion.
     * @return The amount of items that were actually or would have been inserted if simulated.
     */
    default long insert(ItemStack filter, boolean simulate) {
        return insert(filter, filter.getCount(), simulate);
    }

    /**
     * Extracts the given amount of items matching the filter from this container.
     * <p>
     * Keep in mind that the {@link ItemStack} is only acting as a filter to allow for
     * item type and NBT matching. The count of the {@link ItemStack} is completely ignored.<br>
     * Instead, the amount parameter is used to determine how many items to extract.
     * <p>
     * Remember that the extracted amount can exceed the max stack size of the filter.<br>
     * Be sure to create multiple {@link ItemStack}s from the filter if you want to extract
     * more than the max stack size or adjust the amount parameter accordingly.
     *
     * @param filter   The filter to match against.
     * @param amount   The amount of items to extract.
     * @param simulate Whether to simulate the extraction.
     * @return The amount of items that were actually or would have been extracted if simulated.
     */
    long extract(ItemStack filter, long amount, boolean simulate);

    /**
     * Extracts the given items from this container.
     * <p>
     * This method uses the count of the filter as the amount to extract.<br>
     * If you want to define a custom amount, use {@link #extract(ItemStack, long, boolean)}.
     *
     * @param filter   The filter to match against.
     * @param simulate Whether to simulate the extraction.
     * @return The amount of items that were actually or would have been extracted if simulated.
     */
    default long extract(ItemStack filter, boolean simulate) {
        return extract(filter, filter.getCount(), simulate);
    }

    /**
     * Returns whether this container contains at least one item matching the filter.
     *
     * @param filter The filter to match against.
     * @return True if this container contains at least one item matching, false otherwise.
     */
    boolean contains(ItemStack filter);

    /**
     * Clears this container.
     */
    void clear();

    /**
     * Clears this container of items matching the filter.
     *
     * @param filter The filter to match against.
     */
    void clear(ItemStack filter);
}
