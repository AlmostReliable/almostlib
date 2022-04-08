package com.github.almostreliable.lib.registry;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public class DataGenContainer<T> {
    final Map<Function<T, String>, Function<T, String>> langProviders = new HashMap<>();

    public void lang(Function<T, String> keyProvider, Function<T, String> valueProvider) {
        langProviders.put(keyProvider, valueProvider);
    }
}
