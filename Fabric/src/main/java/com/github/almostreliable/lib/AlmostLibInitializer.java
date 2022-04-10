package com.github.almostreliable.lib;

public interface AlmostLibInitializer {
    default void onInitialize() {}

    default void onClientInitialize() {}
}
