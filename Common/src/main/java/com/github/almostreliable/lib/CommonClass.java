package com.github.almostreliable.lib;

import com.github.almostreliable.lib.test.TestRegistry;

public class CommonClass {
    public static void init() {
        if (AlmostLib.INSTANCE.isGameTestMode()) {
            TestRegistry.init();
        }
    }
}
