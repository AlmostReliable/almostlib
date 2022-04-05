package com.github.almostreliable.lib.registry;

import net.minecraft.core.NonNullList;
import net.minecraft.world.item.Item;

import java.util.function.Supplier;

public class Collector {
    protected final NonNullList<Supplier<Item>> items = NonNullList.create();
}
