package com.github.almostreliable.lib.test;

import com.github.almostreliable.lib.AlmostGameTest;
import com.github.almostreliable.lib.AlmostLib;
import com.github.almostreliable.lib.registry.RegistryEntry;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;

@AlmostGameTest(AlmostLib.MOD_ID)
public class RegistryGameTest {
    public static final String EMPTY_STRUCTURE = "almostlib:empty_structure";

    @GameTest(template = EMPTY_STRUCTURE)
    public void dummyItemPresent(GameTestHelper helper) {
        ifPresentCheck(helper, TestRegistry.DUMMY_ITEM);
    }

    @GameTest(template = EMPTY_STRUCTURE)
    public void dummySwordPresent(GameTestHelper helper) {
        ifPresentCheck(helper, TestRegistry.DUMMY_SWORD);
    }

    @GameTest(template = EMPTY_STRUCTURE)
    public void dummyBlockPresent(GameTestHelper helper) {
        ifPresentCheck(helper, TestRegistry.TEST_BLOCK);
    }

    @GameTest(template = EMPTY_STRUCTURE)
    public void dummyBlockLinkPresent(GameTestHelper helper) {
        ifPresentCheck(helper, TestRegistry.TEST_BLOCK_ITEM);
    }

    @GameTest(template = EMPTY_STRUCTURE)
    public void dummyEntityPresent(GameTestHelper helper) {
        ifPresentCheck(helper, TestRegistry.DUMMY_ENTITY);
    }

    private void ifPresentCheck(GameTestHelper helper, RegistryEntry<?> registryEntry) {
        if (registryEntry.isPresent()) {
            helper.succeed();
        } else {
            helper.fail("Failed as " + registryEntry.getRegistryName() + " is not present");
        }
    }
}
