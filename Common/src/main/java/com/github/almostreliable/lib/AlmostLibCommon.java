package com.github.almostreliable.lib;

import com.github.almostreliable.lib.registry.RegistryManager;
import com.github.almostreliable.lib.test.TestRegistry;

import java.util.ArrayList;
import java.util.List;

public class AlmostLibCommon {
    static final List<RegistryManager> MANAGERS = new ArrayList<>();

    public static void init() {
        TestRegistry.init();
        if (AlmostLib.INSTANCE.isGameTestMode()) {
        }
    }
}
