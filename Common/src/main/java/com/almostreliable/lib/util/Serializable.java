package com.almostreliable.lib.util;

import net.minecraft.nbt.Tag;

public interface Serializable<T extends Tag> {

    T serialize();

    void deserialize(T tag);
}
